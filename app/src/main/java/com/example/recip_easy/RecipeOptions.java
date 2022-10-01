package com.example.recip_easy;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

public class RecipeOptions extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView recipeOptions;
    ArrayList<String> recipes = new ArrayList<>();
    String key = "8ad6e9fe49b04bf9b77792e7a305833e";
    String name = "";
    HashMap<String, ArrayList> map = new HashMap<>();

    public RecipeOptions() {

    }

    public static RecipeOptions newInstance(String param1, String param2) {
        RecipeOptions fragment = new RecipeOptions();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.activity_options, container, false);
        recipeOptions = view.findViewById(R.id.listView);

        //name = getActivity().getIntent().getStringExtra("Ingredient");
        name = SearchFragment.name;
        new AsyncThread().execute();
        BottomNavigationView navView = view.findViewById(R.id.nav_view);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = HomeFragment.newInstance("","");
                        break;
                    case R.id.navigation_search:
                        selectedFragment = SearchFragment.newInstance("","");
                        break;
                    case R.id.navigation_user:
                        selectedFragment = UserFragment.newInstance("","");
                        break;
                }
                // load fragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
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
                String s = recipeOptions.getItemAtPosition(i).toString();

                Intent intent = new Intent(getActivity(), LoadRecipe.class);
                intent.putExtra("info", map.get(s));
                intent.putExtra("title", s);

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
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(id);
                    temp.add(image);
                    map.put(t,temp);
                    recipes.add(t);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayAdapter<String> adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, recipes);
            recipeOptions.setAdapter(adapter);
        }
    }


}

