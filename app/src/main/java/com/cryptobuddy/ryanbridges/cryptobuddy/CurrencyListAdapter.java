package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ryan on 12/9/2017.
 */

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> {
    private List<CurrencyListItem> currencyList;
    private CurrencyListAdapter.ViewHolder viewHolder;
    private static CustomItemClickListener listener;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String priceStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;
    private AppCompatActivity context;

    public CurrencyListAdapter(List<CurrencyListItem> currencyList, String negativePercentStringResource, String positivePercentStringResource, String priceStringResource, int positiveGreenColor, int negativeRedColor, AppCompatActivity context, CustomItemClickListener listener) {
        this.currencyList = currencyList;
        this.listener = listener;
        this.negativePercentStringResource = negativePercentStringResource;
        this.positivePercentStringResource = positivePercentStringResource;
        this.priceStringResource = priceStringResource;
        this.positiveGreenColor = positiveGreenColor;
        this.negativeRedColor = negativeRedColor;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(CurrencyListAdapter.ViewHolder holder, int position) {
        CurrencyListItem item = currencyList.get(position);
        if (item.change24hr < 0) {
            holder.changeTextView.setText(String.format(negativePercentStringResource, item.changePCT24hr, item.change24hr));
            holder.changeTextView.setTextColor(negativeRedColor);
        } else {
            holder.changeTextView.setText(String.format(positivePercentStringResource, item.changePCT24hr, item.change24hr));
            holder.changeTextView.setTextColor(positiveGreenColor);
        }
        String[] namePieces = item.fullName.split("(?=\\()");
        holder.fullNameTextView.setText(namePieces[0]);
        holder.symbolNameTextView.setText(namePieces[1]);
        holder.currPriceTextView.setText(String.format(priceStringResource, item.currPrice));
        Picasso.with(context).load(CurrencyListActivity.baseImageURL + item.imageURL).into(holder.coinImageView);
    }

    @Override
    public CurrencyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_list_item_row, parent, false);
        viewHolder = new CurrencyListAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fullNameTextView;
        public TextView changeTextView;
        public TextView currPriceTextView;
        public ImageView coinImageView;
        public TextView symbolNameTextView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.fullName);
            changeTextView = (TextView) itemLayoutView.findViewById(R.id.changeText);
            currPriceTextView = (TextView) itemLayoutView.findViewById(R.id.currPriceText);
            coinImageView = (ImageView) itemLayoutView.findViewById(R.id.coinImage);
            symbolNameTextView = (TextView) itemLayoutView.findViewById(R.id.symbolName);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount() {
        return currencyList.size();
    }

}
