package com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    private AppCompatActivity context;
    public DatabaseHelperSingleton db;
    private CustomItemClickListener listener;

    public AddFavoriteCoinListAdapter(List<CoinMetadata> coinlist, AppCompatActivity context, DatabaseHelperSingleton db, CustomItemClickListener listener) {
        this.coinlist = coinlist;
        this.context = context;
        this.db = db;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(final AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList holder, final int position) {
        CoinMetadata item = coinlist.get(position);
        holder.fullNameTextView.setText(item.fullName);
        CoinFavoritesStructures favs = db.getFavorites();
        if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet.
            holder.favoriteButton.setChecked(false);
        } else { // Coin is a favorite
            holder.favoriteButton.setChecked(true);
        }
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("I", "Inside of favoriteButton Listener");
                holder.favoriteButton.toggle();
                CoinFavoritesStructures favs = db.getFavorites();
                CoinMetadata item = coinlist.get(position);
                if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet. Add it.
                    favs.favoritesMap.put(item.symbol, item.symbol);
                    favs.favoriteList.add(item.symbol);
                } else { // Coin is already a favorite, remove it
                    favs.favoritesMap.remove(item.symbol);
                    favs.favoriteList.remove(item.symbol);
                }
                db.saveCoinFavorites(favs);
            }
        });

    }

    @Override
    public AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_favorites_list_item, parent, false);
        viewHolder = new AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList(itemLayoutView, this.listener);
        return viewHolder;
    }

    public static class ViewHolderFavoriteCoinList extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fullNameTextView;
        public ToggleButton favoriteButton;
        private WeakReference<CustomItemClickListener> listenerRef;

        public ViewHolderFavoriteCoinList(View itemLayoutView, CustomItemClickListener listener) {
            super(itemLayoutView);
            listenerRef = new WeakReference<>(listener);
            itemLayoutView.setOnClickListener(this);
            fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.full_name_coin_favs_textview);
            favoriteButton = (ToggleButton) itemLayoutView.findViewById(R.id.favorite_button);
        }

        @Override
        public void onClick(View v) {
            favoriteButton.toggle();
            listenerRef.get().onItemClick(getAdapterPosition(), v);
        }
    }

    public int getItemCount() {
        return coinlist.size();
    }
}
