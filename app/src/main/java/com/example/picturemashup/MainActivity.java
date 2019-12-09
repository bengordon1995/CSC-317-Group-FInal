package com.example.picturemashup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.graphics.PorterDuff.Mode.SRC_IN;

public class MainActivity extends AppCompatActivity {

    static String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int imageDim = 900;

    //self reference
    static MainActivity instance;

    //the users image bitmap taken from the camera intent
    Bitmap cameraBitmap;


    //-------------------------------------------------------------------------
    //                      Camera Intent and Image Capture
    //-------------------------------------------------------------------------

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.instance = this;
    }

    public void onTakePictureClick(View view){
        dispatchTakePictureIntent();
    }

    //sets up and dispatches the camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            // Continue only if the File was successfully created
            if (photoFile != null) {
                //need to use a file provider to avoid the exposed uri error
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //if the picture was successfully taken, we know its stored in the static variable directory
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            //start editing activity
            sendImageToEditor();
        }
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        //set class variable so current path is accessible outside method scope
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    //launches the editing activity and passes the location of the camera image file
    public void sendImageToEditor(){
        Intent intent = new Intent(this, EditingActivity.class);
        Bundle extras = new Bundle();
        extras.putString("fileLocation", currentPhotoPath);
        intent.putExtras(extras);
        startActivity(intent);
    }

    //takes the uri string and launches a share intent with it
    private void sendScreenshot(String uriString) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriString));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
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
}


