package com.example.picturemashup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.graphics.PorterDuff.Mode.SRC_IN;

import static android.graphics.PorterDuff.Mode.SRC_OVER;

public class CroppingActivity extends Activity {

    Bitmap bp;
    String bitmapAbsoluteLocation;
    Canvas bitmapCanvas;
    DrawView drawImg;
    LinearLayout ln1;
    Bitmap bitmap;
    String BackgroundFileLocation;

    /*
        When this activity is started, it is passed a string bundle that represents
        the absolute file location of the image from the camera intent from the previous activity

        onCreate saves this location, and creates a drawView specified below
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        //get camera intent bitmap from extras
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        bitmapAbsoluteLocation = extras.getString("fileLocation");
        BackgroundFileLocation= extras.getString("BackgroundFileLocation");

        ln1 = (LinearLayout) findViewById(R.id.mainViewGroup);
        drawImg = new DrawView(this);
        ln1.addView(drawImg);

    }


    /*
        This drawView class allows the user to erase the image from the camera intent,
        thereby non-square cropping the image
     */
    public class DrawView extends View implements View.OnTouchListener {

        private int x = 0;
        private int y = 0;
        Path circlePath;
        Paint circlePaint;

        private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        private final Paint eraserPaint = new Paint();


        public DrawView(Context context){
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            this.setOnTouchListener(this);

            // Set background
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bp = BitmapFactory.decodeFile(bitmapAbsoluteLocation, bmOptions);

            //squaring up the image
            bp = bp.createBitmap(bp, 0, 0, CameraActivity.imageDim, CameraActivity.imageDim);

            //rotating the image into correct alignment
            bp = rotateBitmap(bp, 90);


            // Create and set bitmap to draw on
            bitmap = Bitmap.createBitmap(CameraActivity.imageDim, CameraActivity.imageDim, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas();
            bitmapCanvas.setBitmap(bitmap);
            bitmapCanvas.drawColor(Color.TRANSPARENT);
            bitmapCanvas.drawBitmap(bp, 0, 0, null);

            circlePath = new Path();
            circlePaint = new Paint();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            // Set eraser paint properties
            eraserPaint.setAlpha(0);
            eraserPaint.setStrokeJoin(Paint.Join.ROUND);
            eraserPaint.setStrokeCap(Paint.Cap.ROUND);
            eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            eraserPaint.setAntiAlias(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
            bitmapCanvas.drawCircle(x, y, 30, eraserPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        public boolean onTouch(View view, MotionEvent event) {
            x = (int) event.getX();
            y = (int) event.getY();

            bitmapCanvas.drawCircle(x, y, 30, eraserPaint);
            circlePath.reset();
            circlePath.addCircle(x, y, 30, Path.Direction.CW);

            int ac=event.getAction();
            switch(ac){
                case MotionEvent.ACTION_UP:
                    circlePath.reset();
                    break;
            }
            invalidate();
            return true;
        }
    }

    /*
        onCropSubmit creates a new file for the cropped image, and saves the bitmap to the file
        Then, it calls compositeImages() with the file
     */
    public void onCropSubmit(View view) throws IOException {
        System.out.println("called onCropSubmit");
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            //set class variable so current path is accessible outside method scope
            String editedBitmapFilePath = image.getAbsolutePath();

            FileOutputStream outputStream = new FileOutputStream(image);

            //draw edits to bitmap
            Paint paint = new Paint();
            Bitmap outBitmap = Bitmap.createBitmap(CameraActivity.imageDim, CameraActivity.imageDim, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outBitmap);
            canvas.drawBitmap(bp, null, new Rect(0,0, CameraActivity.imageDim, CameraActivity.imageDim), paint);
            paint.setXfermode(new PorterDuffXfermode(SRC_IN));
            canvas.drawBitmap(bitmap, null, new Rect(0,0, CameraActivity.imageDim, CameraActivity.imageDim), paint);

            outBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            //call function to combine images
            compositeImages(editedBitmapFilePath, outBitmap);
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /*
        Combines the two images: first the background from Flickr, and then the cropped foreground
        from the camera intent
     */

    public void compositeImages(String bm2FileLocation, Bitmap editedBitmap){
        System.out.println("called composite images");
        //get the bitmap from the editingActivity
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bmForeground = BitmapFactory.decodeFile(bm2FileLocation, bmOptions);

        //dummy bitmap (THIS IS WHERE THE FLICKR BITMAP GOES)
        Bitmap bmBackground = BitmapFactory.decodeFile(BackgroundFileLocation, bmOptions);

        //create a new bitmap to hold the composited image
        Paint paint = new Paint();
        Bitmap outBitmap = Bitmap.createBitmap(CameraActivity.imageDim, CameraActivity.imageDim, Bitmap.Config.ARGB_8888);

        //composite the two images by drawing to canvas (NOT CHECKED FOR PORTER-DUFF YET)
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bmBackground, null, new Rect(0,0, CameraActivity.imageDim, CameraActivity.imageDim), paint);
        paint.setXfermode(new PorterDuffXfermode(SRC_OVER));
        canvas.drawBitmap(editedBitmap, null, new Rect(0,0, CameraActivity.imageDim, CameraActivity.imageDim), paint);

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );


            //set class variable so current path is accessible outside method scope
            String newBackgroundImagePath = image.getAbsolutePath();
            FileOutputStream outputStream = new FileOutputStream(image);


            outBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Intent intent = new Intent(this, EditingActivity.class);
            Bundle extras = new Bundle();
            extras.putString("fileLocation", newBackgroundImagePath);
            intent.putExtras(extras);
            startActivity(intent);

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(IOException f){
            f.printStackTrace();
        }
    }

    /*
         Method to rotate bitmap, as android default from emulator rotates 90 degrees
    */
    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void share(View view){

    }
}
