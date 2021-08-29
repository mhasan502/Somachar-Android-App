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

    String data = "";
    String heading;
    String imagelink;
    String newslink;
    String papername;
    String time;
    String details;


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
                data = data + line;
            }
            if (data.equals("[]null")) {
                return null;
            }
            MainActivity.news.clear();
            try {
                JSONArray JA = new JSONArray(data);
                for (int i = 0; i < JA.length(); i++) {         // Extract data from JSON
                    JSONObject JO = (JSONObject) JA.get(i);

                    heading = JO.get("heading").toString();
                    imagelink = JO.get("imagelink").toString();
                    newslink = JO.get("newslink").toString();
                    papername = JO.get("papername").toString();
                    time = JO.get("time").toString();
                    details = JO.get("details").toString();
                    Log.e("Heading", heading);

                    // Date
                    Locale locale = new Locale("en", "US");
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, locale);
                    Date dt = simpleDateFormat.parse(time);
                    assert dt != null;                      // asserts that the object is not null

                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
                    String dates = dateFormat.format(dt);
                    DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
                    String times = timeFormat.format(dt);
                    String date = times + "  " + dates;

                    News item = new News(heading, imagelink, newslink, papername, date, details);
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
    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        if (data.equals("[]null")) {
            Toast.makeText(context, "দুঃখিত। কোনো ফলাফল পাওয়া যায়নি", Toast.LENGTH_LONG).show();
        }
        MainActivity.newsAdapter.notifyDataSetChanged();
    }
}
