package com.example.somachar

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class FetchNews(
    @field:SuppressLint("StaticFieldLeak") var context: Context
) : AsyncTask<String?, String?, String?>() {
    var data = StringBuilder()

    private var heading: String? = null
    private var imageLink: String? = null
    private var newsLink: String? = null
    private var paperName: String? = null
    private var time: String? = null
    private var details: String? = null

    // Background Operation
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: String?): String? {
        try {
            val url = URL(p0[0])
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String? = ""
            while (line != null) {
                line = bufferedReader.readLine()
                data.append(line)
            }
            if (data.isEmpty()) {
                return null
            }
            MainActivity.news.clear()

            try {
                val jsonArray = JSONArray(data.toString())
                for (i in 0 until jsonArray.length()) {         // Extract data from JSON
                    val jsonObject = jsonArray[i] as JSONObject
                    heading = jsonObject["heading"].toString()
                    imageLink = jsonObject["imagelink"].toString()
                    newsLink = jsonObject["newslink"].toString()
                    paperName = jsonObject["papername"].toString()
                    time = jsonObject["time"].toString()
                    details = jsonObject["details"].toString()
                    Log.e("Heading", heading!!)

                    // Date
                    val locale = Locale("en", "US")
                    val pattern = "yyyy-MM-dd'T'HH:mm:ss"
                    val dt = time?.let { SimpleDateFormat(pattern, locale).parse(it) }!!

                    val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
                    val timeFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale)

                    val dates = dateFormat.format(dt)
                    val times = timeFormat.format(dt)

                    val date = "$times  $dates"
                    val item = News(heading!!, imageLink!!, newsLink!!, paperName!!, date, details!!)
                    MainActivity.news.add(item)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    // Result from background operation to MainActivity
    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onPostExecute(aVoid: String?) {
        super.onPostExecute(aVoid)

        if (data.isEmpty()) {
            Toast.makeText(context, "দুঃখিত। কোনো ফলাফল পাওয়া যায়নি", Toast.LENGTH_LONG).show()
        }
        MainActivity.newsAdapter!!.notifyDataSetChanged()
    }

}