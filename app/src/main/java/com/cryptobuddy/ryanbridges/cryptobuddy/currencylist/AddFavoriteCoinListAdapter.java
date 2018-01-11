package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.DatabaseHelperSingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Ryan on 12/31/2017.
 */

public class AddFavoriteCoinListAdapter extends RecyclerView.Adapter<AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList> {
    private List<CoinMetadata> coinlist;
    private AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList viewHolder;
    private WeakReference<AppCompatActivity> contextRef;
    private WeakReference<DatabaseHelperSingleton> dbRef;
    private CustomItemClickListener listener;
    private Drawable starDisabled;
    private Drawable starEnabled;

    public AddFavoriteCoinListAdapter(List<CoinMetadata> coinlist, AppCompatActivity context, DatabaseHelperSingleton db, CustomItemClickListener listener) {
        this.coinlist = coinlist;
        this.contextRef = new WeakReference<>(context);
        this.dbRef = new WeakReference<>(db);
        this.listener = listener;
        this.starDisabled = contextRef.get().getResources().getDrawable(R.drawable.ic_star_border_black_24dp);
        this.starEnabled = contextRef.get().getResources().getDrawable(R.drawable.ic_star_enabled_24dp);
    }

    @Override
    public void onBindViewHolder(final AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList holder, final int position) {
        CoinMetadata item = coinlist.get(position);
        holder.fullNameTextView.setText(item.fullName);
        CoinFavoritesStructures favs = dbRef.get().getFavorites();
        if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet.
            holder.favoriteButton.setBackground(starDisabled);
        } else { // Coin is a favorite
            holder.favoriteButton.setBackground(starEnabled);
        }
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.favoriteButton.getBackground() == starDisabled) {
                    holder.favoriteButton.setBackground(starEnabled);
                }
                else {
                    holder.favoriteButton.setBackground(starDisabled);
                }
                CoinFavoritesStructures favs = dbRef.get().getFavorites();
                CoinMetadata item = coinlist.get(position);
                if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet. Add it.
                    favs.favoritesMap.put(item.symbol, item.symbol);
                    favs.favoriteList.add(item.symbol);
                } else { // Coin is already a favorite, remove it
                    favs.favoritesMap.remove(item.symbol);
                    favs.favoriteList.remove(item.symbol);
                }
                dbRef.get().saveCoinFavorites(favs);
            }
        });

    }

    @Override
    public AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_favorites_list_item, parent, false);
        viewHolder = new AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList(itemLayoutView, this.listener, starEnabled, starDisabled);
        return viewHolder;
    }

    public static class ViewHolderFavoriteCoinList extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fullNameTextView;
        public ImageView favoriteButton;
        private WeakReference<CustomItemClickListener> listenerRef;
        private Drawable starEnabled, starDisabled;

        public ViewHolderFavoriteCoinList(View itemLayoutView, CustomItemClickListener listener, Drawable starEnabled, Drawable starDisabled) {
            super(itemLayoutView);
            this.listenerRef = new WeakReference<>(listener);
            itemLayoutView.setOnClickListener(this);
            this.fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.full_name_coin_favs_textview);
            this.favoriteButton = (ImageView) itemLayoutView.findViewById(R.id.fav_coin_row_favorite_button);
            this.starDisabled = starDisabled;
            this.starEnabled = starEnabled;
        }

        @Override
        public void onClick(View v) {
            if (favoriteButton.getBackground() == starDisabled) {
                favoriteButton.setBackground(starEnabled);
            } else {
                favoriteButton.setBackground(starDisabled);
            }
            listenerRef.get().onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount() {
        return coinlist.size();
    }
}
