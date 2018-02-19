package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.markets;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.MarketNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.MarketsResponse;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPair;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPairNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CryptoCompareCoinService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

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
    private String tsymbol = null;
    private String fsymbol = null;
    private List<String> pairs;
    private RecyclerView marketsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MarketsListAdapter adapter;
    private List<MarketNode> markets;
    private Context mContext;
    public static final String BASE_MARKET_URL_CHROME_URL = "https://www.cryptocompare.com/exchanges/binance/overview/%s";

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

    public void getPairMarket() {
        CryptoCompareCoinService.getPairsMarket(getActivity(), tsymbol, fsymbol, new afterTaskCompletion<MarketsResponse>() {
            @Override
            public void onTaskCompleted(MarketsResponse marketsResponse) {
                markets.clear();
                markets.addAll(marketsResponse.getData().getMarketsList());
                adapter.setMarketsList(marketsResponse.getData().getMarketsList());
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public void getTopPairs() {
        CryptoCompareCoinService.getTopPairs(getActivity(), symbol, new afterTaskCompletion<TradingPair>() {
            @Override
            public void onTaskCompleted(TradingPair tradingPair) {
                swipeRefreshLayout.setRefreshing(true);
                pairs.clear();
                for (TradingPairNode node : tradingPair.getData()) {
                    pairs.add(node.getFromSymbol() + "/" + node.getToSymbol());
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pairs);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerArrayAdapter);
                if (fsymbol == null || tsymbol == null) {
                    // TODO: Fix errors with MIOTA
                    String symbols[] = pairs.get(0).split("/");
                    fsymbol = symbols[1];
                    tsymbol = symbols[0];
                }
                getPairMarket();
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
        marketsRecyclerView = (RecyclerView) rootView.findViewById(R.id.markets_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        marketsRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        marketsRecyclerView.setLayoutManager(llm);
        pairs = new ArrayList<>();
        markets = new ArrayList<>();
        adapter = new MarketsListAdapter(markets, (AppCompatActivity) mContext, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                String marketStr = markets.get(position).getMarket();
                customTabsIntent.launchUrl(getActivity(), Uri.parse(String.format(BASE_MARKET_URL_CHROME_URL, marketStr)));
            }
        });
        marketsRecyclerView.setAdapter(adapter);
        spinner = (Spinner) rootView.findViewById(R.id.top_pairs_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String symbols[] = pairs.get(position).split("/");
                fsymbol = symbols[1];
                tsymbol = symbols[0];
                getPairMarket();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.markets_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getTopPairs();
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getPairMarket();
    }

}
