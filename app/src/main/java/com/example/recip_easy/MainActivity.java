package com.example.recip_easy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Bottom Navigation View.
        BottomNavigationView navView = findViewById(R.id.nav_view);

        //Pass the ID's of Different destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_user, R.id.navigation_search )
                .build();

        //Initialize NavController.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
       // navGraph.setStartDestination(R.id.navigation_search);
       // navController.setGraph(navGraph);
       // NavigationUI.setupWithNavController(binding.bottomNavView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


    }
}