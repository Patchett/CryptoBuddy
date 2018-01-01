package com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

import java.util.List;

/**
 * Created by Ryan on 12/31/2017.
 */

public class AddFavoriteCoinListAdapter extends RecyclerView.Adapter<AddFavoriteCoinListAdapter.ViewHolder> {
    private List<CoinMetadata> currencyList;
    private AddFavoriteCoinListAdapter.ViewHolder viewHolder;
    private static CustomItemClickListener listener;
    private AppCompatActivity context;

    public AddFavoriteCoinListAdapter(List<CoinMetadata> currencyList, AppCompatActivity context, CustomItemClickListener listener) {
        this.currencyList = currencyList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(AddFavoriteCoinListAdapter.ViewHolder holder, int position) {
        CoinMetadata item = currencyList.get(position);
        String[] namePieces = item.fullName.split("(?=\\()");
        holder.fullNameTextView.setText(namePieces[0]);
        holder.symbolNameTextView.setText(namePieces[1]);
    }

    @Override
    public AddFavoriteCoinListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_currency_list_item, parent, false);
        viewHolder = new AddFavoriteCoinListAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView fullNameTextView;
        private TextView symbolNameTextView;
        private ImageButton favoriteButton;

        private ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);
            fullNameTextView = (TextView) itemLayoutView.findViewById(R.id.fullName);
            symbolNameTextView = (TextView) itemLayoutView.findViewById(R.id.symbolName);
            favoriteButton = (ImageButton) itemLayoutView.findViewById(R.id.favorite_button);
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
