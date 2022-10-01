package com.example.recip_easy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView recipeOptions;
    //ArrayList<String> recipes =  new ArrayList<>();
    ArrayList<Recipe> recipes =  new ArrayList<>();
    Context context;
    String key = "8ad6e9fe49b04bf9b77792e7a305833e";
   // HashMap<String,ArrayList> map = new HashMap<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recipeOptions = view.findViewById(R.id.trend);

        new AsyncThread().execute();

        recipeOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = recipeOptions.getItemAtPosition(i).toString();

               // Intent intent = new Intent(getActivity(), LoadRecipe.class);
               // intent.putExtra("info", map.get(s));
               // intent.putExtra("title", s);

                Intent intent = new Intent(getActivity(), LoadRecipe.class);
                intent.putExtra("id", recipes.get(i).getID());
                intent.putExtra("title", recipes.get(i).getTitle());
                intent.putExtra("img", recipes.get(i).getImage());

                startActivity(intent);
            }
        });

        return view;
    }

    public class AsyncThread extends AsyncTask<String, Void, Void> {
        String allData = "";
        URL url;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL("https://api.spoonacular.com/recipes/random?apiKey=" + key + "&number=5");
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
                allData = input.readLine();
                Log.d("hi",allData);
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
                JSONObject recipe = new JSONObject(allData);
                JSONArray data = recipe.getJSONArray("recipes");
                Log.d("p1",data.toString());
                for (int i = 0; i < data.length(); i++) {
                    JSONObject individual = data.getJSONObject(i);
                    String t = individual.get("title").toString();
                    String id = individual.get("id").toString();
                    String image = individual.get("image").toString();
                    recipes.add(new Recipe(t,id,image));
                    //ArrayList<String> temp = new ArrayList<>();
                   // temp.add(id);
                    //temp.add(image);
                   // map.put(t,temp);
                    //recipes.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, recipes);
            //recipeOptions.setAdapter(adapter);
            CustomAdapter adapter = new CustomAdapter(getActivity(),recipes);
            recipeOptions.setAdapter(adapter);
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
