package com.example.picturemashup;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class flickrImageSearchActivity extends AppCompatActivity {
    private String top= "https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=4ed60a6252f6a97f733595aa0f6a9875&tags=";
    private String bottom= "&extras=url_s&format=json&nojsoncallback=1";

    private String baseUrl;

    public ArrayList<Bitmap> pictures= new ArrayList<Bitmap>();
    public ArrayList<String> picturesSRC= new ArrayList<String>();

    TextView temp;

    private myAdapter badAdapter;
    ListView badCharactersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);

    }


    public void getJson(View v){
        //LinearLayout linearLayout = (LinearLayout)findViewById(R.id.cameraLayout);
        //linearLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        pictures= new ArrayList<Bitmap>();
        EditText input= findViewById(R.id.input);
        String search= input.getText().toString();
        System.out.println(search);
        baseUrl = top + search + bottom;
        new DownloadTask().execute();
    }



    private class DownloadTask extends AsyncTask<Object, Void, JSONObject> {

        /**
         * This method receives a json line and adds the author, article name, article website, url
         * preview of the article into an Arraylist that contains a hashmap inside.
         * @param objects
         */
        @Override
        protected JSONObject doInBackground(Object[] objects) {
            try {

                String json = "";
                String line;

                URL url = new URL(baseUrl);

                System.out.println(baseUrl);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                while ((line = in.readLine()) != null) {
                    System.out.println("JSON LINE " + line);
                    json += line;
                }
                in.close();
                System.out.println("hello");
                System.out.println(json);

                JSONObject jsonObject = new JSONObject(json);
                JSONObject pics= jsonObject.getJSONObject("photos");
                JSONArray allPhotos= pics.getJSONArray("photo");

                for(int i=0; i< 30; i++){

                    String picURL= allPhotos.getJSONObject(i).getString("url_s");
                    Bitmap pic= getBitmapFromURL(picURL);
                    pic = Bitmap.createScaledBitmap(pic, 500, 500, true);
                    pictures.add(pic);
                    picturesSRC.add(picURL);




                }

                System.out.println(pictures.size());
                System.out.println(picturesSRC.size());

                return jsonObject;
            }

            catch (Exception e) { e.printStackTrace(); }

            return null;
        }


        @Override
        protected void onPostExecute(JSONObject json) {

            badAdapter = new myAdapter(getApplicationContext(), R.layout.title_list, pictures);
            badCharactersListView = findViewById(R.id.imageViews);
            System.out.println("made it to onPost");
            badCharactersListView.setAdapter(badAdapter);

        }



        public Bitmap getBitmapFromURL(String src) {
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }



    }


}
