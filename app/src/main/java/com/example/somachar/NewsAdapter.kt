package com.example.somachar

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class NewsAdapter(
    private val context: Context,
    private val newsList: List<News>,
    private val textToSpeech: TextToSpeech
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    // Meta data of each news
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heading: TextView
        val textDate: TextView
        val paperName: Button
        val imageView: ImageButton

        init {
            heading = itemView.findViewById(R.id.heading)
            paperName = itemView.findViewById(R.id.papername)
            imageView = itemView.findViewById(R.id.imageLink)
            textDate = itemView.findViewById(R.id.textDate)
        }
    }

    // Initialize ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val newsView = inflater.inflate(R.layout.news_layout, parent, false)
        return ViewHolder(newsView)
    }

    // Add contents in each news
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news = newsList[position]

        val headingText: TextView = holder.heading
        headingText.text = news.heading

        val time: TextView = holder.textDate
        time.text = news.time

        val paperNameButton: Button = holder.paperName
        paperNameButton.text = news.paperName
        paperNameButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(news.newsLink))
            context.startActivity(browserIntent)
        }

        val imageLink: ImageView = holder.imageView
        Picasso.get().load(news.imageLink).into(imageLink)
        imageLink.clipToOutline = true
        imageLink.setOnClickListener {
            if (textToSpeech.isSpeaking) {
                Toast.makeText(context, "সংবাদটি বলা বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
                textToSpeech.stop()
            } else {
                Toast.makeText(context, news.heading, Toast.LENGTH_SHORT).show()
                textToSpeech.speak(news.details, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    // Total number of news
    override fun getItemCount() = newsList.size
}
