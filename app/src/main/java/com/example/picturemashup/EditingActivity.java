package com.example.picturemashup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;

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

    @Override
    public void onResume(){
        super.onResume();
        LinearLayout ln1= findViewById(R.id.editingViewMainGroup);
        ln1.setVisibility(View.VISIBLE);
    }

    /*
        onClick for the camera button in the Editing Activity: launches the Camera Activity, which
        on resolution of the subsequent cropping activity, will return to this activity
     */
    public void launchCameraIntent(View view){
        Intent intent = new Intent(this, CameraActivity.class);
        Bundle extras = new Bundle();
        extras.putString("BackgroundLocation", bitmapAbsoluteLocation);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void launchShareIntent(View view){
        System.out.println("Called launchShareIntent");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setContainerActivity(this);
        Bundle bundle = new Bundle();
        bundle.putString("imageAbsoluteLocation", bitmapAbsoluteLocation);
        contactsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.contactFrame, contactsFragment);
        LinearLayout ln2 = findViewById(R.id.contactFrame);
        ln2.setVisibility(View.VISIBLE);
        LinearLayout ln1= findViewById(R.id.editingViewMainGroup);
        ln1.setVisibility(View.GONE);
        fragmentTransaction.commit();

    }

    public void restart(View view){
        Intent intent = new Intent(this, flickrImageSearchActivity.class);
        startActivity(intent);
    }
}
