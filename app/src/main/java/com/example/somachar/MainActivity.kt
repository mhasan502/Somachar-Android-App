package com.example.somachar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import me.ibrahimsn.particle.ParticleView
import java.util.Locale


class MainActivity : AppCompatActivity(), OnInitListener, OnRefreshListener {
    private var backPressedTime: Long = 0
    private val RECOGNIZER_RESULT = 1
    private var textView: TextView? = null
    private var textToSpeech: TextToSpeech? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var particleView: ParticleView? = null
    private var url = "https://somachar.fly.dev/api/news"


    // Initialization of startup activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        particleView = findViewById(R.id.particleView)
        textView = findViewById(R.id.speechView)
        textToSpeech = TextToSpeech(this, this)
        swipeRefreshLayout = findViewById(R.id.swipeContainer)

        val recyclerView = findViewById<RecyclerView>(R.id.rvNews)

        // Clear previously loaded news to avoid duplication
        if (!isSearchContent) {
            FetchNews(this).execute(url)
        }
        newsAdapter = NewsAdapter(this, news, textToSpeech!!)
        recyclerView.adapter = newsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // UI
        window.navigationBarColor = getColor(R.color.background_color)
    }

    // Send information between Activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RECOGNIZER_RESULT) {         // Search news based on user speech
            if (data != null) {
                val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val result = matches!![0]
                textView!!.text = result
                FetchNews(this).execute("$url/$result")
                isSearchContent = true
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // Speech listener
    fun speechToText(view: View?) {
        textToSpeech!!.stop()
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn_BD")
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "bn_BD")
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn_BD")
        speechIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "bn_BD")
        speechIntent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, "bn_BD")
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "বলুন")
        startActivityForResult(speechIntent, RECOGNIZER_RESULT)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech!!.language = Locale("bn_BD", "BD")
            textToSpeech!!.setSpeechRate(1f)
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(
                this@MainActivity,
                "Error occurred while initializing Text-To-Speech engine",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefresh() {
        newsAdapter!!.notifyDataSetChanged()
        swipeRefreshLayout!!.isRefreshing = false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        textToSpeech!!.stop()
        if (backPressedTime + 500 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
            if (isSearchContent) {
                FetchNews(this).execute(url)
                isSearchContent = false
            }
            newsAdapter!!.notifyDataSetChanged()
        }
        backPressedTime = System.currentTimeMillis()
    }

    public override fun onPause() {
        textToSpeech!!.stop()
        particleView!!.pause()
        super.onPause()
    }

    public override fun onResume() {
        particleView!!.resume()
        super.onResume()
    }

    public override fun onDestroy() {
        textToSpeech!!.stop()
        textToSpeech!!.shutdown()
        super.onDestroy()
    }

    companion object {
        var news: MutableList<News> = ArrayList()
        private var isSearchContent = false

        @SuppressLint("StaticFieldLeak")
        var newsAdapter: NewsAdapter? = null
    }
}
