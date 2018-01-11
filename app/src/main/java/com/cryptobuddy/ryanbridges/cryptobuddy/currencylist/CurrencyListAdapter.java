package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.DatabaseHelperSingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Ryan on 12/9/2017.
 */

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<CurrencyListItem> currencyList;
    private CurrencyListAdapter.ViewHolder viewHolder;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String priceStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;
    private CustomItemClickListener listener;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelperSingleton> dbRef;
    private Hashtable<String, CurrencyListItem> currencyItemMap;

    public CurrencyListAdapter(List<CurrencyListItem> currencyList, Hashtable<String, CurrencyListItem> currencyItemMap,
                               DatabaseHelperSingleton db, String negativePercentStringResource,
                               String positivePercentStringResource, String priceStringResource, int positiveGreenColor,
                               int negativeRedColor, AppCompatActivity context, CustomItemClickListener listener) {
        this.currencyList = currencyList;
        this.negativePercentStringResource = negativePercentStringResource;
        this.positivePercentStringResource = positivePercentStringResource;
        this.priceStringResource = priceStringResource;
        this.positiveGreenColor = positiveGreenColor;
        this.negativeRedColor = negativeRedColor;
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
        this.dbRef = new WeakReference<>(db);
        this.currencyItemMap = currencyItemMap;
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
        Picasso.with(contextRef.get()).load(CurrencyListActivity.baseImageURL + item.imageURL).into(holder.coinImageView);
    }

    @Override
    public CurrencyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_list_item, parent, false);
        viewHolder = new CurrencyListAdapter.ViewHolder(itemLayoutView, listener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        public TextView fullNameTextView;
        public TextView changeTextView;
        public TextView currPriceTextView;
        public ImageView coinImageView;
        public TextView symbolNameTextView;
        private CustomItemClickListener listener;

        public ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.fullName);
            changeTextView = (TextView) itemLayoutView.findViewById(R.id.changeText);
            currPriceTextView = (TextView) itemLayoutView.findViewById(R.id.currPriceText);
            coinImageView = (ImageView) itemLayoutView.findViewById(R.id.coinImage);
            symbolNameTextView = (TextView) itemLayoutView.findViewById(R.id.symbolName);
            this.listener = listener;
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        this.currencyItemMap.remove(this.currencyList.get(position).symbol);
        this.currencyList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < this.currencyList.size() && toPosition < this.currencyList.size()) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(this.currencyList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(this.currencyList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        CoinFavoritesStructures favs = this.dbRef.get().getFavorites();
        favs.favoriteList.clear();
        favs.favoritesMap.clear();
        for (int i = 0; i < currencyList.size(); i++) {
            String currSymbol = currencyList.get(i).symbol;
            favs.favoriteList.add(currSymbol);
            favs.favoritesMap.put(currSymbol, currSymbol);
        }
        Log.d("I", "favs.favoriteList: " + favs.favoriteList);
        this.dbRef.get().saveCoinFavorites(favs);
    }

    public int getItemCount() {
        return currencyList.size();
    }

    public void setCurrencyList(List<CurrencyListItem> newCurrencyList) {
        this.currencyList = newCurrencyList;
    }

}
