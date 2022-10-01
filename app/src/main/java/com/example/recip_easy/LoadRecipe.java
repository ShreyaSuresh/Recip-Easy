package com.example.recip_easy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class LoadRecipe extends AppCompatActivity {
    String id;
    String img;
    Context context;
    String title;
    String key = "8ad6e9fe49b04bf9b77792e7a305833e";

    Button start;
    Button add;
    ImageView picture;
    TextView recipeName;
    TextView summary;

    JSONArray steps = new JSONArray();

    JSONObject recipe = new JSONObject();
    String allData = "";
    public static ArrayList<Recipe> list = new ArrayList<>();
    String text="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        start = findViewById(R.id.start);
        picture = findViewById(R.id.imageView);
        add = findViewById(R.id.later);
        recipeName = findViewById(R.id.recipe_name);
        summary = findViewById(R.id.summary);

        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        img = getIntent().getStringExtra("img");
       // id=info.get(0);
       // img = info.get(1);
        Log.d("ID",id);
        Log.d("IMG",img);

        recipeName.setText(title);

        context = this;

        new AsyncThread().execute();

        new InfoAsyncThread().execute();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RecipeSteps.class);
                intent.putExtra("steps", steps.toString());

                startActivity(intent);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput("info.json",MODE_PRIVATE));

                    try {
                        recipe.put("Title",title);
                        recipe.put("Image",img);
                        recipe.put("ID",id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    writer.write(recipe.toString());
                    writer.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput("info.json")));
                    text = bufferedReader.readLine();
                    Log.d("JSON Text",text);
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject object = null;
                try {
                    object = new JSONObject(text);
                    String title = object.get("Title").toString();
                    String img = object.get("Image").toString();
                    String id = object.get("ID").toString();
                    list.add((new Recipe(title,id,img)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }
    public class InfoAsyncThread extends AsyncTask<String, Void, Void> {

        URL url;
        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL("https://api.spoonacular.com/recipes/"+id+"/summary?apiKey=" + key);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
                allData = input.readLine();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONObject data = new JSONObject(allData);
                String s = data.get("summary").toString();
                summary.setText(Html.fromHtml(s));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class AsyncThread extends AsyncTask<String, Void, Void> {

        URL url;
        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL("https://api.spoonacular.com/recipes/"+id+"/analyzedInstructions?apiKey=" + key);
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
                allData = input.readLine();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                JSONArray data = new JSONArray(allData);
                JSONObject individual = data.getJSONObject(0);
                steps = (JSONArray) individual.get("steps");
                new ImageAsyncThread(picture).execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class ImageAsyncThread extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;
        public ImageAsyncThread(ImageView imgView){
            imageView=imgView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String imgURL = img;
            Bitmap bitmap = null;
            InputStream in;
            try {
                in = new URL(imgURL).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
            //imageView.getLayoutParams().height = 700;
            //imageView.getLayoutParams().width = 500;
        }
    }

}
