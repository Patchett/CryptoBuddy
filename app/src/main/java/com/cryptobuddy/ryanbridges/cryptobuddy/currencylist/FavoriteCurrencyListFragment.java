package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;

/**
 * Created by Ryan on 1/21/2018.
 */

public class FavoriteCurrencyListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;

    public FavoriteCurrencyListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoriteCurrencyListFragment newInstance() {
        return new FavoriteCurrencyListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorite_currency_list, container, false);
        return rootView;
    }

    @Override
    public void onRefresh() {
    }
}

