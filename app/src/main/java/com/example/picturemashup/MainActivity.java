package com.example.picturemashup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class MainActivity extends AppCompatActivity {

    ImageView currentImage;
    static String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    //the users image bitmap taken from the camera intent
    Bitmap cameraBitmap;


    //-------------------------------------------------------------------------
    //                      Camera Intent and Image Capture
    //-------------------------------------------------------------------------

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            //squaring up the image, sizing images handled by XML as long as image is square
            Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, size, size);
            ImageView mainImageView = findViewById(R.id.currentImageView);
            mainImageView.setImageBitmap(squareBitmap);
            this.cameraBitmap = squareBitmap;
        }
    }

    private File createImageFile() throws IOException {
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


    public void captureCollage(View view){
        try {
            //get the bitmap from the main image view
            ImageView mainImageView = findViewById(R.id.currentImageView);
            Bitmap bitmap = ((BitmapDrawable)mainImageView.getDrawable()).getBitmap();

            //square up the bitmap
            int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
            Bitmap squareBitmap = Bitmap.createBitmap(bitmap, 0, 0, size, size);

            //make a new file to hold the image bitmap
            File outFile = createImageFile();

            //write the cropped bitmap to the file
            FileOutputStream outputStream = new FileOutputStream(outFile);
            squareBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Uri uri = FileProvider.getUriForFile(
                    MainActivity.this,
                    "com.example.android.fileprovider", //(use your app signature + ".provider" )
                    outFile);
            sendScreenshot(uri.toString());
        }catch(IOException e){}
    }

    //takes the uri string and launches a share intent with it
    private void sendScreenshot(String uriString) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriString));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
    }
}


