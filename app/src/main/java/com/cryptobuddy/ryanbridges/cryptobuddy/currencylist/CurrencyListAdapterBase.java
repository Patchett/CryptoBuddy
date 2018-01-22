package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.drawable.Drawable;
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
import java.util.List;

/**
 * Created by Ryan on 1/22/2018.
 */

public class CurrencyListAdapterBase extends RecyclerView.Adapter<CurrencyListAdapterBase.ViewHolder> {
    private List<CMCCoin> currencyList;
    private CurrencyListAdapterBase.ViewHolder viewHolder;
    private String negativePercentStringResource;
    private String positivePercentStringResource;
    private String priceStringResource;
    private String mktCapStringResource;
    private String volumeStringResource;
    private int positiveGreenColor;
    private int negativeRedColor;
    private CustomItemClickListener rowListener;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelperSingleton> dbRef;
    private Drawable starDisabled;
    private Drawable starEnabled;

    public CurrencyListAdapterBase(List<CMCCoin> currencyList,
                               DatabaseHelperSingleton db, AppCompatActivity context, CustomItemClickListener listener) {
        this.currencyList = currencyList;
        this.contextRef = new WeakReference<>(context);
        this.rowListener = listener;
        this.dbRef = new WeakReference<>(db);
        this.mktCapStringResource = this.contextRef.get().getString(R.string.mkt_cap_format);
        this.volumeStringResource = this.contextRef.get().getString(R.string.volume_format);
        this.negativePercentStringResource = this.contextRef.get().getString(R.string.negative_pct_change_format);
        this.positivePercentStringResource = this.contextRef.get().getString(R.string.positive_pct_change_format);
        this.priceStringResource = this.contextRef.get().getString(R.string.price_format);
        this.negativeRedColor = this.contextRef.get().getResources().getColor(R.color.percentNegativeRed);
        this.positiveGreenColor = this.contextRef.get().getResources().getColor(R.color.percentPositiveGreen);
        this.starDisabled = contextRef.get().getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        this.starEnabled = contextRef.get().getResources().getDrawable(R.drawable.ic_star_enabled_24dp);
    }

    public void setFavoriteButtonClickListener(final CurrencyListAdapterBase.ViewHolder holder, final int position) {
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.starButton.getBackground() == starDisabled) {
                    holder.starButton.setBackground(starEnabled);
                }
                else {
                    holder.starButton.setBackground(starDisabled);
                }
                CoinFavoritesStructures favs = dbRef.get().getFavorites();
                CMCCoin item = currencyList.get(position);
                if (favs.favoritesMap.get(item.getSymbol()) == null) { // Coin is not a favorite yet. Add it.
                    favs.favoritesMap.put(item.getSymbol(), item.getSymbol());
                    favs.favoriteList.add(item.getSymbol());
                } else { // Coin is already a favorite, remove it
                    favs.favoritesMap.remove(item.getSymbol());
                    favs.favoriteList.remove(item.getSymbol());
                }
                dbRef.get().saveCoinFavorites(favs);
            }
        });
    }

    @Override
    public void onBindViewHolder(final CurrencyListAdapterBase.ViewHolder holder, final int position) {
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
        Picasso.with(contextRef.get()).load(String.format(CurrencyListTabsActivity.IMAGE_URL_FORMAT, item.getId())).into(holder.currencyListCoinImageView);
        CoinFavoritesStructures favs = this.dbRef.get().getFavorites();
        if (favs.favoritesMap.get(item.getSymbol()) != null) {
            holder.starButton.setBackground(starEnabled);
        } else {
            holder.starButton.setBackground(starDisabled);
        }
        setFavoriteButtonClickListener(holder, position);
    }

    @Override
    public CurrencyListAdapterBase.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_list_item, parent, false);
        viewHolder = new CurrencyListAdapterBase.ViewHolder(itemLayoutView, rowListener);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView currencyListfullNameTextView;
        private TextView currencyListChangeTextView;
        private TextView currencyListCurrPriceTextView;
        private TextView currencyListVolumeTextView;
        private TextView currencyListMarketcapTextView;
        private ImageView currencyListCoinImageView;
        protected ImageView starButton;
        private CustomItemClickListener listener;

        private ViewHolder(View itemLayoutView, CustomItemClickListener listener)
        {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            currencyListfullNameTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListfullNameTextView);
            currencyListChangeTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListChangeTextView);
            currencyListCurrPriceTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListCurrPriceTextView);
            currencyListCoinImageView = (ImageView) itemLayoutView.findViewById(R.id.currencyListCoinImageView);
            currencyListVolumeTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListVolumeTextView);
            currencyListMarketcapTextView = (TextView) itemLayoutView.findViewById(R.id.currencyListMarketcapTextView);
            starButton = (ImageView) itemLayoutView.findViewById(R.id.currencyListFavStar);
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount() {
        return currencyList.size();
    }

    public void setCurrencyList(List<CMCCoin> newCurrencyList) {
        this.currencyList = newCurrencyList;
    }

}
