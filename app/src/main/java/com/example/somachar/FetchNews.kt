package com.example.somachar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FetchNews extends AsyncTask<String, String, String> {

    StringBuilder data = new StringBuilder();
    String heading, imageLink, newsLink, paperName, time, details;


    // Constructor
    @SuppressLint("StaticFieldLeak")
    Context context;
    public FetchNews (Context context) {
        this.context = context;
    }

    // Background Operation
    @Override
    protected String doInBackground(String... Strings) {
        try {
            URL url = new URL(Strings[0]);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data.append(line);
            }
            if (data.length() == 0) {
                return null;
            }
            MainActivity.news.clear();
            try {
                JSONArray jsonArray = new JSONArray(data.toString());
                for (int i = 0; i < jsonArray.length(); i++) {         // Extract data from JSON
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                    heading = jsonObject.get("heading").toString();
                    imageLink = jsonObject.get("imagelink").toString();
                    newsLink = jsonObject.get("newslink").toString();
                    paperName = jsonObject.get("papername").toString();
                    time = jsonObject.get("time").toString();
                    details = jsonObject.get("details").toString();
                    Log.e("Heading", heading);

                    // Date
                    Locale locale = new Locale("en", "US");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
                    Date dt = simpleDateFormat.parse(time);

                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);

                    assert dt != null;
                    String dates = dateFormat.format(dt);
                    String times = timeFormat.format(dt);
                    String date = times + "  " + dates;

                    News item = new News(heading, imageLink, newsLink, paperName, date, details);
                    MainActivity.news.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Result from background operation to MainActivity
    @Override @SuppressLint("NotifyDataSetChanged")
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if (data.length() == 0) {
            Toast.makeText(context, "দুঃখিত। কোনো ফলাফল পাওয়া যায়নি", Toast.LENGTH_LONG).show();
        }
        MainActivity.newsAdapter.notifyDataSetChanged();
    }
}
