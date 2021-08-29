package com.example.somachar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    Context context;
    private final List<News> newsList;
    private final TextToSpeech textToSpeech;

    // Constructor
    public NewsAdapter(Context context,List<News> newsList, TextToSpeech textToSpeech) {
        this.context = context;
        this.newsList = newsList;
        this.textToSpeech = textToSpeech;
    }

    // Meta data of each news
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView heading, textDate;
        Button paperName;
        ImageButton imageView;

        // Constructor
        public ViewHolder(View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.heading);
            paperName = itemView.findViewById(R.id.papername);
            imageView = itemView.findViewById(R.id.imageLink);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }

    // Initialize ViewHolder
    @NotNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View newsView = inflater.inflate(R.layout.news_layout, parent, false);
        return new ViewHolder(newsView);
    }

    // Add contents in each news
    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, int position) {
        News news = newsList.get(position);

        TextView headingText = holder.heading;
        headingText.setText(news.getHeading());

        Button paperNameButton = holder.paperName;
        paperNameButton.setText(news.getPapername());
        paperNameButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getNewslink()));
            context.startActivity(browserIntent);
        });

        ImageView imageLink = holder.imageView;
        Picasso.get().load(news.getImagelink()).into(imageLink);
        imageLink.setClipToOutline(true);
        imageLink.setOnClickListener(v -> {
            if (textToSpeech.isSpeaking()) {
                Toast.makeText(context, "সংবাদটি বলা বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show();
                textToSpeech.stop();
            }
            else{
                Toast.makeText(context, news.getHeading(), Toast.LENGTH_SHORT).show();
                textToSpeech.speak(news.getDetails(), TextToSpeech.QUEUE_FLUSH, null,null);
                Log.e("",news.getDetails());
            }
        });
        TextView time = holder.textDate;
        time.setText(news.getTime());
    }

    // Total number of news
    @Override
    public int getItemCount() {
        return newsList.size();
    }
}