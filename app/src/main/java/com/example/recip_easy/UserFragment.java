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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    ArrayList<Recipe> arrayList= LoadRecipe.list;
    ListView listView;
    TextView title;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        listView = view.findViewById(R.id.myList);
        title = view.findViewById(R.id.textView_title);
        title.setText(SignIn.user + "'s Saved Recipes");

       // ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,arrayList);
       // listView.setAdapter(arrayAdapter);
        CustomAdapter adapter = new CustomAdapter(getActivity(),arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = listView.getItemAtPosition(i).toString();

                // Intent intent = new Intent(getActivity(), LoadRecipe.class);
                // intent.putExtra("info", map.get(s));
                // intent.putExtra("title", s);

                Intent intent = new Intent(getActivity(), LoadRecipe.class);
                intent.putExtra("id", arrayList.get(i).getID());
                intent.putExtra("title", arrayList.get(i).getTitle());
                intent.putExtra("img", arrayList.get(i).getImage());

                startActivity(intent);
            }
        });

        return view;
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

    public class ImageAsyncThread extends AsyncTask<String,Void, Bitmap> {
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