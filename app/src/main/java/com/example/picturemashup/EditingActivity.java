package com.example.picturemashup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

public class EditingActivity extends Activity {

    Bitmap bp;
    Canvas bitmapCanvas;
    //DrawView drawImg;
    LinearLayout ln1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get camera intent bitmap from extras
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String fileLocation = extras.getString("fileLocation");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + fileLocation);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bp = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        //
        ImageView ln1 = (ImageView) findViewById(R.id.currentImageView);
        ln1.setImageBitmap(bp);
        //drawImg = new DrawView(this);
        //ln1.addView(drawImg);
    }

//    public class DrawView extends View implements View.OnTouchListener {
//
//        private int x = 0;
//        private int y = 0;
//        Bitmap bitmap;
//        Path circlePath;
//        Paint circlePaint;
//
//        private final Paint paint = new Paint();
//        private final Paint eraserPaint = new Paint();
//
//
//        public DrawView(Context context){
//            super(context);
//            setFocusable(true);
//            setFocusableInTouchMode(true);
//            this.setOnTouchListener(this);
//
//            // Set background
//            this.setBackgroundColor(Color.CYAN);
//
//            // Set bitmap
//            bitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
//            bitmapCanvas = new Canvas();
//            bitmapCanvas.setBitmap(bitmap);
//            bitmapCanvas.drawColor(Color.TRANSPARENT);
//            bitmapCanvas.drawBitmap(bp, 0, 0, null);
//
//            circlePath = new Path();
//            circlePaint = new Paint();
//            circlePaint.setAntiAlias(true);
//            circlePaint.setColor(Color.BLUE);
//            circlePaint.setStyle(Paint.Style.STROKE);
//            circlePaint.setStrokeJoin(Paint.Join.MITER);
//            circlePaint.setStrokeWidth(4f);
//
//            // Set eraser paint properties
//            eraserPaint.setAlpha(0);
//            eraserPaint.setStrokeJoin(Paint.Join.ROUND);
//            eraserPaint.setStrokeCap(Paint.Cap.ROUND);
//            eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//            eraserPaint.setAntiAlias(true);
//
//        }
//
//        @Override
//        public void onDraw(Canvas canvas) {
//
//            canvas.drawBitmap(bitmap, 0, 0, paint);
//            bitmapCanvas.drawCircle(x, y, 30, eraserPaint);
//
//            canvas.drawPath(circlePath, circlePaint);
//        }
//
//        public boolean onTouch(View view, MotionEvent event) {
//            x = (int) event.getX();
//            y = (int) event.getY();
//
//            bitmapCanvas.drawCircle(x, y, 30, eraserPaint);
//
//            circlePath.reset();
//            circlePath.addCircle(x, y, 30, Path.Direction.CW);
//
//            int ac=event.getAction();
//            switch(ac){
//                case MotionEvent.ACTION_UP:
//                    circlePath.reset();
//                    break;
//            }
//            invalidate();
//            return true;
//        }
//    }
//
//    public void onCropSubmit(View view){
//
//    }
}
