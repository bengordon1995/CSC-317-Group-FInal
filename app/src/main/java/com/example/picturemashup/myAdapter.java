package com.example.picturemashup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.picturemashup.CameraActivity.currentPhotoPath;

public class myAdapter extends ArrayAdapter<String> {


    private ArrayList<Bitmap> items;
    Context c;

    public myAdapter(Context context, int textViewResourceId, ArrayList items) {
        super(context, textViewResourceId, items);
        c = context;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.title_list, null);
        }
        Bitmap id = items.get(position);
        ImageView iv = (ImageView) v.findViewById(R.id.each_image);
        if (iv != null) {
            iv.setImageBitmap(id);
            iv.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    ImageView imageview= (ImageView) v;
                    Bitmap imgaeBitmap= ((BitmapDrawable)imageview.getDrawable()).getBitmap();


                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    File storageDir = c.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File image = null;
                    String editedBitmapFilePath= null;
                    try {
                        image = File.createTempFile(
                                imageFileName,  /* prefix */
                                ".jpg",         /* suffix */
                                storageDir      /* directory */
                        );

                    //set class variable so current path is accessible outside method scope
                    editedBitmapFilePath = image.getAbsolutePath();

                    FileOutputStream outputStream = new FileOutputStream(image);
                    imgaeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }




                    Intent intent = new Intent(c, EditingActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("fileLocation", editedBitmapFilePath);
                    intent.putExtras(extras);
                    c.startActivity(intent);
                }
            });
        }
        return v;
    }
}