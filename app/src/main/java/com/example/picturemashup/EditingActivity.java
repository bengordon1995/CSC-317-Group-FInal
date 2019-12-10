package com.example.picturemashup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditingActivity extends AppCompatActivity {

    String bitmapAbsoluteLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editing_layout);

        //get camera intent bitmap from extras
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        bitmapAbsoluteLocation = extras.getString("fileLocation");



        // Set background
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bp = BitmapFactory.decodeFile(bitmapAbsoluteLocation, bmOptions);
        ImageView image= findViewById(R.id.pasteImage);
        image.setImageBitmap(bp);


    }


    public void launchCameraIntent(View view){


        Intent intent = new Intent(this, CameraActivity.class);
        Bundle extras = new Bundle();
        extras.putString("BackgroundLocation", bitmapAbsoluteLocation);
        intent.putExtras(extras);
        startActivity(intent);


    }




}
