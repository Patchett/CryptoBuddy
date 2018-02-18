package com.cryptobuddy.ryanbridges.cryptobuddy.markets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPair;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CryptoCompareCoinService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import static com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.GraphFragment.ARG_SYMBOL;

/**
 * Created by Ryan on 12/29/2017.
 */

public class MarketsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private String symbol;

    public MarketsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MarketsFragment newInstance(String symbol) {
        MarketsFragment fragment = new MarketsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SYMBOL, symbol);
        fragment.setArguments(args);
        return fragment;
    }

    public void getTopPairs() {
        CryptoCompareCoinService.getTopPairs(getActivity(), symbol, new afterTaskCompletion<TradingPair>() {
            @Override
            public void onTaskCompleted(TradingPair tradingPair) {
                Log.d("I", "tradingPair: " + tradingPair.getData());
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_markets, container, false);
        symbol = getArguments().getString(ARG_SYMBOL);
        getTopPairs();
        return rootView;
    }

    @Override
    public void onRefresh() {
    }

}
