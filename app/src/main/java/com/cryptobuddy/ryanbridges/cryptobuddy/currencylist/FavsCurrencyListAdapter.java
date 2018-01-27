package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Ryan on 12/9/2017.
 */

public class FavsCurrencyListAdapter extends CurrencyListAdapterBase {
    private ArrayList<CMCCoin> currencyList;
    private FavsCurrencyListAdapter.ViewHolder viewHolder;
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

    public FavsCurrencyListAdapter(ArrayList<CMCCoin> currencyList, DatabaseHelperSingleton db, AppCompatActivity context, CustomItemClickListener listener) {
        super(currencyList, db, context, listener);
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

    public void setFavoriteButtonClickListener(final FavsCurrencyListAdapter.ViewHolder holder, final int position) {
        holder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoinFavoritesStructures favs = dbRef.get().getFavorites();
                CMCCoin item = currencyList.get(position);
                favs.favoritesMap.remove(item.getSymbol());
                favs.favoriteList.remove(item.getSymbol());
                dbRef.get().saveCoinFavorites(favs);
                currencyList.remove(position);
                notifyDataSetChanged();
            }
        });
    }
}
