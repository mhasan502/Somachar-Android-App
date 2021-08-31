package com.example.somachar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.ibrahimsn.particle.ParticleView;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SwipeRefreshLayout.OnRefreshListener {

    public static List<News> news = new ArrayList<>();
    private static boolean isSearchContent = false;
    long backPressedTime;
    final int RECOGNIZER_RESULT = 1;

    @SuppressLint("StaticFieldLeak")                // suppress warnings
    public static NewsAdapter newsAdapter;

    TextView textView;
    TextToSpeech textToSpeech;
    SwipeRefreshLayout swipeRefreshLayout;
    ParticleView particleView;
    String url = "https://somachar.herokuapp.com/api/news";


    // Initialization of startup activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        particleView = findViewById(R.id.particleView);
        textView = findViewById(R.id.speechView);
        textToSpeech = new TextToSpeech(this, this);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = findViewById(R.id.rvNews);

        if (!isSearchContent) {             // Clear previously loaded news to avoid duplication
            new FetchNews(this).execute(url);
        }

        newsAdapter = new NewsAdapter(this, news, textToSpeech);
        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // UI
        getWindow().setNavigationBarColor(getColor(R.color.background_color));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // Send information between Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (requestCode == RECOGNIZER_RESULT) {         // Search news based on user speech
                if (data != null) {
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String result = matches.get(0);
                    textView.setText(result);
                    new FetchNews(this).execute(url + '/' + result);
                    isSearchContent = true;
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Initialization of speech engine
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {           // Check speech engine is available
            textToSpeech.setLanguage(new Locale("bn_BD", "BD"));
            textToSpeech.setSpeechRate(1);
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(MainActivity.this, "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
        }
    }

    // Speech listener
    public void speechToText(View view) {
        textToSpeech.stop();
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn_BD");
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "bn_BD");
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn_BD");
        speechIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "bn_BD");
        speechIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "bn_BD");
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "বলুন");
        startActivityForResult(speechIntent, RECOGNIZER_RESULT);
    }

    // Action when the user will swipe
    @Override
    public void onRefresh() {
        newsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    // Action when the user will press back button
    @Override
    public void onBackPressed() {
        textToSpeech.stop();
        if (backPressedTime + 500 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            if (isSearchContent) {
                new FetchNews(this).execute(url);
                isSearchContent = false;
            }
            newsAdapter.notifyDataSetChanged();
        }
        backPressedTime = System.currentTimeMillis();
    }

    // Action when the app will be on pause
    @Override
    public void onPause() {
        textToSpeech.stop();
        particleView.pause();
        super.onPause();
    }

    // Action after resuming from pause
    @Override
    public void onResume() {
        particleView.resume();
        super.onResume();
    }

    // Action when the app will be stopped
    @Override
    public void onDestroy() {
        textToSpeech.stop();
        textToSpeech.shutdown();
        super.onDestroy();
    }

}