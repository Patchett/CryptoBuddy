package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Ryan on 12/9/2017.
 */

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private List<CMCCoin> currencyList;
    private CurrencyListAdapter.ViewHolder viewHolder;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String priceStringResource;
    private String mktCapStringResource;
    private String volumeStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;
    private CustomItemClickListener listener;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelperSingleton> dbRef;
    private Hashtable<String, CMCCoin> currencyItemMap;

    public CurrencyListAdapter(List<CMCCoin> currencyList, Hashtable<String, CMCCoin> currencyItemMap,
                               DatabaseHelperSingleton db, AppCompatActivity context, CustomItemClickListener listener) {
        this.currencyList = currencyList;
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
        this.dbRef = new WeakReference<>(db);
        this.currencyItemMap = currencyItemMap;
        this.mktCapStringResource = this.contextRef.get().getString(R.string.mkt_cap_format);
        this.volumeStringResource = this.contextRef.get().getString(R.string.volume_format);
        this.negativePercentStringResource = this.contextRef.get().getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = this.contextRef.get().getString(R.string.positive_pct_change_format);
        this.priceStringResource = this.contextRef.get().getString(R.string.price_format);
        this.negativeRedColor = this.contextRef.get().getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = this.contextRef.get().getResources().getColor(R.color.percentPositiveGreen);
    }

    @Override
    public void onBindViewHolder(CurrencyListAdapter.ViewHolder holder, int position) {
        CMCCoin item = currencyList.get(position);
        if (item.getPercent_change_24h() == null) {
            holder.currencyListChangeTextView.setText("N/A");
            holder.currencyListChangeTextView.setTextColor(positiveGreenColor);
        } else {
            double dayChange = Double.parseDouble(item.getPercent_change_24h());
            if (dayChange < 0) {
                holder.currencyListChangeTextView.setText(String.format(negativePercentStringResource, dayChange));
                holder.currencyListChangeTextView.setTextColor(negativeRedColor);
            } else {
                holder.currencyListChangeTextView.setText(String.format(positivePercentStringResource, dayChange));
                holder.currencyListChangeTextView.setTextColor(positiveGreenColor);
            }
        }
        if (item.getMarket_cap_usd() == null) {
            holder.currencyListMarketcapTextView.setText("N/A");
        } else {
            holder.currencyListMarketcapTextView.setText(String.format(mktCapStringResource, Double.parseDouble(item.getMarket_cap_usd())));
        }
        if (item.getVolume_usd_24h() == null) {
            holder.currencyListVolumeTextView.setText("N/A");
        } else {
            holder.currencyListVolumeTextView.setText(String.format(volumeStringResource, Double.parseDouble(item.getVolume_usd_24h())));
        }
        if (item.getPrice_usd() == null) {
            holder.currencyListCurrPriceTextView.setText("N/A");
        } else {
            holder.currencyListCurrPriceTextView.setText(String.format(priceStringResource, Double.parseDouble(item.getPrice_usd())));
        }
        holder.currencyListfullNameTextView.setText(item.getSymbol());
        Picasso.with(contextRef.get()).load(String.format(CurrencyListActivity.IMAGE_URL_FORMAT, item.getId())).into(holder.currencyListCoinImageView);
    }

    @Override
    public CurrencyListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_list_item, parent, false);
        viewHolder = new CurrencyListAdapter.ViewHolder(itemLayoutView, listener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        public TextView currencyListfullNameTextView;
        public TextView currencyListChangeTextView;
        public TextView currencyListCurrPriceTextView;
        public TextView currencyListVolumeTextView;
        public TextView currencyListMarketcapTextView;
        public ImageView currencyListCoinImageView;
        private CustomItemClickListener listener;

        public ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            currencyListfullNameTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListfullNameTextView);
            currencyListChangeTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListChangeTextView);
            currencyListCurrPriceTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListCurrPriceTextView);
            currencyListCoinImageView = (ImageView) itemLayoutView.findViewById(R.id.currencyListCoinImageView);
            currencyListVolumeTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListVolumeTextView);
            currencyListMarketcapTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListMarketcapTextView);
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
        this.currencyItemMap.remove(this.currencyList.get(position).getSymbol());
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
            String currSymbol = currencyList.get(i).getSymbol();
            favs.favoriteList.add(currSymbol);
            favs.favoritesMap.put(currSymbol, currSymbol);
        }
        this.dbRef.get().saveCoinFavorites(favs);
    }

    public int getItemCount() {
        return currencyList.size();
    }

    public void setCurrencyList(List<CMCCoin> newCurrencyList) {
        this.currencyList = newCurrencyList;
    }

}
