package com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;

import java.util.List;

/**
 * Created by Ryan on 12/31/2017.
 */

public class AddFavoriteCoinListAdapter extends RecyclerView.Adapter<AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList> {
    private List<CoinMetadata> coinlist;
    private AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList viewHolder;
    private AppCompatActivity context;

    public AddFavoriteCoinListAdapter(List<CoinMetadata> coinlist, AppCompatActivity context) {
        this.coinlist = coinlist;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList holder, int position) {
        CoinMetadata item = coinlist.get(position);
        holder.fullNameTextView.setText(item.fullName);
    }

    @Override
    public AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coin_favorites_list_item, parent, false);
        viewHolder = new AddFavoriteCoinListAdapter.ViewHolderFavoriteCoinList(itemLayoutView);
        return viewHolder;
    }

    public static class ViewHolderFavoriteCoinList extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView fullNameTextView;
        public ToggleButton favoriteButton;

        public ViewHolderFavoriteCoinList(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.full_name_coin_favs_textview);
            favoriteButton = (ToggleButton) itemLayoutView.findViewById(R.id.favorite_button);
        }

        @Override
        public void onClick(View v) {
            favoriteButton.toggle();
        }
    }

    public int getItemCount() {
        return coinlist.size();
    }
}
