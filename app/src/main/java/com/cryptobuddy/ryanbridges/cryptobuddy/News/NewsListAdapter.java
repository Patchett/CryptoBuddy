package com.cryptobuddy.ryanbridges.cryptobuddy.News;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;


/**
 * Created by Ryan on 8/13/2017.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private List<NewsItem> newsList;
    private ViewHolder viewHolder;
    private Context context;
    private static CustomItemClickListener listener;

    public NewsListAdapter(List<NewsItem> newsList, Context context, CustomItemClickListener listener) {
        this.newsList = newsList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(NewsListAdapter.ViewHolder holder, int position) {
        NewsItem currNewsItem = newsList.get(position);
        holder.articleTitleTextView.setText(currNewsItem.articleTitle);
        String publishTimeString = (String) DateUtils.getRelativeTimeSpanString((long) currNewsItem.publishedOn * 1000, System.currentTimeMillis(), MINUTE_IN_MILLIS);
        holder.ageTextView.setText(publishTimeString);
        holder.sourceNameTextView.setText(currNewsItem.sourceName);
        Picasso.with(context).load(currNewsItem.imageURL).into(holder.articleImageView);
    }

    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_row, parent, false);
        viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView articleTitleTextView;
        private ImageView articleImageView;
        private TextView ageTextView;
        private TextView sourceNameTextView;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            articleTitleTextView = (TextView) itemLayoutView.findViewById(R.id.articleTitle);
            ageTextView = (TextView) itemLayoutView.findViewById(R.id.age);
            articleImageView = (ImageView) itemLayoutView.findViewById(R.id.articleImage);
            sourceNameTextView = (TextView) itemLayoutView.findViewById(R.id.sourceName);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }


    public int getItemCount() {
        return newsList.size();
    }

}
