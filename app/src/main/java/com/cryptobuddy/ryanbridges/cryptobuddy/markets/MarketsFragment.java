package com.cryptobuddy.ryanbridges.cryptobuddy.markets;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.MarketNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.MarketsResponse;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPair;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPairNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CryptoCompareCoinService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import java.util.ArrayList;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.GraphFragment.ARG_SYMBOL;

/**
 * Created by Ryan on 12/29/2017.
 */

public class MarketsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private String symbol;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;

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

    public void getPairMarket(String fsymbol, String tsymbol) {
        CryptoCompareCoinService.getPairsMarket(getActivity(), tsymbol, fsymbol, new afterTaskCompletion<MarketsResponse>() {
            @Override
            public void onTaskCompleted(MarketsResponse marketsResponse) {
                for (MarketNode node : marketsResponse.getData().getMarketsList()) {
                    Log.d("I", "marketNode: " + node);
                }
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
            }
        });
    }

    public void getTopPairs() {
        CryptoCompareCoinService.getTopPairs(getActivity(), symbol, new afterTaskCompletion<TradingPair>() {
            @Override
            public void onTaskCompleted(TradingPair tradingPair) {
                List<String> pairs = new ArrayList<>();
                for (TradingPairNode node : tradingPair.getData()) {
                    pairs.add(node.getFromSymbol() + "/" + node.getToSymbol());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pairs);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                String symbols[] = pairs.get(0).split("/");
                getPairMarket(symbols[1], symbols[0]);
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
        spinner = (Spinner) rootView.findViewById(R.id.top_pairs_spinner);
        getTopPairs();
        return rootView;
    }

    @Override
    public void onRefresh() {
    }

}
