package com.example.recip_easy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Timer;

public class RecipeSteps extends AppCompatActivity {
    TextView stepText;
    TextView timer;
    Button button;
    Button startTimer;
    Button stopTimer;
    TextView stepNum;
    String number,unit;
    CountDownTimer t;
    boolean time = true;
    int i = 0;
    JSONArray steps;
    int len;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        stepText = findViewById(R.id.step);
        button = findViewById(R.id.next);
        stepNum = findViewById(R.id.textView2);
        timer = findViewById(R.id.timer);
        startTimer = findViewById(R.id.startTimer);
        stopTimer = findViewById(R.id.stopTimer);
        context = this;

        String s = getIntent().getStringExtra("steps");

        try {
            steps = new JSONArray(s);
            len = steps.length();
            //Log.d("len", steps.length() + "");

            JSONObject parts = steps.getJSONObject(0);
            String step = parts.get("step").toString();
            stepText.setText(step);

            JSONObject length = parts.getJSONObject("length");
            if(time){
                Log.d("timer bool val",time+"");
                number = length.get("number").toString();
                unit = length.get("unit").toString();
                timer.setText(number + " " + unit);
                startTimer.setVisibility(View.VISIBLE);
                stopTimer.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            time = false;
            startTimer.setVisibility(View.INVISIBLE);
            stopTimer.setVisibility(View.INVISIBLE);
            timer.setText("");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if(i==len-1) {
                    button.setVisibility(View.INVISIBLE);
                }
                try {
                    Log.d("clicked","clicked");
                    int stepCount=i+1;
                    stepNum.setText("STEP "+ stepCount);
                    Log.d("hello?",i + " " + len);
                    JSONObject parts = steps.getJSONObject(i);
                    String step = parts.get("step").toString();
                    stepText.setText(step);
                    time = true;
                    JSONObject length = parts.getJSONObject("length");

                    if(time){
                        number = length.get("number").toString();
                        unit = length.get("unit").toString();
                        timer.setText(number + " " + unit);
                        startTimer.setVisibility(View.VISIBLE);
                        stopTimer.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    timer.setText("");
                    startTimer.setVisibility(View.INVISIBLE);
                    stopTimer.setVisibility(View.INVISIBLE);
                    time = false;
                }
            }
        });

        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time=0;
                time = Integer.parseInt(number)*60000;
                Log.d("going in timer","yoohoo");
                t = new CountDownTimer(time,1000) {
                    @Override
                    public void onTick(long l) {
                        Log.d("timer",l+"");
                        long minutes = l/60000;
                        long seconds = l/1000;
                        timer.setText(minutes + " : " + seconds);
                    }

                    @Override
                    public void onFinish() {
                        startTimer.setVisibility(View.INVISIBLE);
                    }
                };
                t.start();
            }
        });
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                t.cancel();
            }
        });
    }
}
