package com.example.picturemashup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

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
                    ImageView image= (ImageView) v;
                    Bitmap imgaeBitmap= ((BitmapDrawable)image.getDrawable()).getBitmap();

                    Intent intent = new Intent(this, CroppingActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("fileLocation", currentPhotoPath);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }
        return v;
    }
}