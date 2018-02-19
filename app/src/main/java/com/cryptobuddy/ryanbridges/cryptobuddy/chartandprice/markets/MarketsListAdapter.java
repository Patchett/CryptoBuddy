package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.markets;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.MarketNode;

import java.util.List;


/**
 * Created by Ryan on 2/18/2018.
 */

public class MarketsListAdapter extends RecyclerView.Adapter<MarketsListAdapter.ViewHolder> {

    private CustomItemClickListener listener;
    private MarketsListAdapter.ViewHolder viewHolder;
    private List<MarketNode> markets;

    public MarketsListAdapter(List<MarketNode> markets, CustomItemClickListener listener) {
        this.markets = markets;
        this.listener = listener;
    }


    @Override
    public void onBindViewHolder(final MarketsListAdapter.ViewHolder holder, final int position) {
        MarketNode node = markets.get(position);
        holder.exchangeNameTextView.setText(node.getMarket());
        // TODO: Use strings.xml and use currency specific symbols
        holder.volDataTextView.setText(Float.toString(node.getVolume24h()));
        holder.changeTextView.setText(Float.toString(node.getChangePct24h()));
        holder.priceTextView.setText(Float.toString(node.getPrice()));
    }

    @Override
    public MarketsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_market_list, parent, false);
        viewHolder = new MarketsListAdapter.ViewHolder(itemLayoutView, listener);
        return viewHolder;
    }

    public int getItemCount() {
        return markets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private CustomItemClickListener listener;
        private TextView exchangeNameTextView;
        private TextView volTitleTextView;
        private TextView volDataTextView;
        private TextView changeTextView;
        private TextView priceTextView;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            this.exchangeNameTextView = (TextView) itemLayoutView.findViewById(R.id.exchangeNameTextView);
            this.volDataTextView = (TextView) itemLayoutView.findViewById(R.id.volDataTextView);
            this.changeTextView = (TextView) itemLayoutView.findViewById(R.id.changeTextView);
            this.priceTextView = (TextView) itemLayoutView.findViewById(R.id.priceTextView);
            this.volTitleTextView = (TextView) itemLayoutView.findViewById(R.id.volTitleTextView);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            Log.d("I", "in onClick market");
            this.listener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setMarketsList(List<MarketNode> newMarketsList) {
        this.markets.clear();
        this.markets.addAll(newMarketsList);
    }
}
