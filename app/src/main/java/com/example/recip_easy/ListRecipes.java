package com.example.recip_easy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListRecipes extends AppCompatActivity{
    ListView recipeOptions;
   // ArrayList<String> recipes =  new ArrayList<>();
    ArrayList<Recipe> recipes =  new ArrayList<>();
    Context context;
    String key = "8ad6e9fe49b04bf9b77792e7a305833e";
    String name = "";
    //HashMap<String,ArrayList> map = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        recipeOptions = findViewById(R.id.listView);
        context = this;

        name = getIntent().getStringExtra("Ingredient");

        new AsyncThread().execute();

        BottomNavigationView navView = findViewById(R.id.nav_view);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance("", "");
                        break;
                    case R.id.navigation_search:
                        selectedFragment = SearchFragment.newInstance("", "");
                        break;
                    case R.id.navigation_user:
                        selectedFragment = UserFragment.newInstance("", "");
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                //frame_container is your layout name in xml file
                transaction.replace(R.id.test, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });


        recipeOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent intent = new Intent(context, LoadRecipe.class);
                intent.putExtra("id", recipes.get(i).getID());
                intent.putExtra("title", recipes.get(i).getTitle());
                intent.putExtra("img", recipes.get(i).getImage());


                startActivity(intent);
            }
        });
    }

    public class AsyncThread extends AsyncTask<String, Void, Void> {
        String allData = "";
        URL url;

        @Override
        protected Void doInBackground(String... strings) {
            try {

                url = new URL("https://api.spoonacular.com/recipes/findByIngredients?apiKey=" + key + "&ingredients=" + name+"&number=5");
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
                recipes.clear();
                JSONArray data = new JSONArray(allData);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject individual = data.getJSONObject(i);
                    String t = individual.get("title").toString();
                    String id = individual.get("id").toString();
                    String image = individual.get("image").toString();
                   // ArrayList<String> temp = new ArrayList<>();
                    //temp.add(id);
                  //  temp.add(image);
                   // map.put(t,temp);
                    recipes.add(new Recipe(t,id,image));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CustomAdapter adapter = new CustomAdapter(context,recipes);
            recipeOptions.setAdapter(adapter);
           // ArrayAdapter<String> adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, android.R.id.text1, recipes);
           // recipeOptions.setAdapter(adapter);
        }
    }

    public class CustomAdapter extends ArrayAdapter<Recipe>{
        public CustomAdapter(@NonNull Context context, ArrayList<Recipe> arrayList) {
            super(context, 0, arrayList);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View currentItemView = convertView;

            if (currentItemView == null) {
                currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.options_list, parent, false);
            }

            Recipe current = getItem(position);

            ImageView img = currentItemView.findViewById(R.id.recipe_icon);
            new ImageAsyncThread(img).execute(current.getImage());

            TextView textView1 = currentItemView.findViewById(R.id.recipe_title);
            textView1.setText(current.getTitle());

            return currentItemView;
        }
    }

    public class ImageAsyncThread extends AsyncTask<String,Void, Bitmap>{
        ImageView imageView;
        public ImageAsyncThread(ImageView imgView){
            imageView=imgView;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String imgURL = strings[0];
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
        }
    }
}
