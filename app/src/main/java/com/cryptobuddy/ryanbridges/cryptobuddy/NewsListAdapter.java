package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * Created by Ryan on 8/13/2017.
 */

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {

    private List<NewsItem> newsList;
    ViewHolder viewHolder;
    private static CustomItemClickListener listener;


    public NewsListAdapter(List<NewsItem> newsList, CustomItemClickListener listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(NewsListAdapter.ViewHolder holder, int position) {
        NewsItem currNewsItem = newsList.get(position);
        holder.articleTitle.setText(currNewsItem.articleTitle);
        holder.articleBody.setText(currNewsItem.articleBody);
    }

    @Override
    public NewsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_row, parent, false);
        viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView articleTitle;
        public TextView articleBody;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            articleTitle = (TextView) itemLayoutView.findViewById(R.id.articleTitle);
            articleBody = (TextView) itemLayoutView.findViewById(R.id.articleBody);
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
