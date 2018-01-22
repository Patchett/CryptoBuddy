package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.CurrencyDetailsTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.NewsListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Ryan on 1/21/2018.
 */

public class AllCurrencyListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private CurrencyListAdapter adapter;
    private List<CMCCoin> currencyItemList;
    private Hashtable<String, CMCCoin> currencyItemMap;
    private DatabaseHelperSingleton db;
    private MenuItem searchItem;
    private SearchView searchView;
    private View rootView;


    public AllCurrencyListFragment() {
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }

    public static AllCurrencyListFragment newInstance() {
        return new AllCurrencyListFragment();
    }

    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinMarketCapService.getAllCoins(getActivity(), new afterTaskCompletion<CMCCoin[]>() {
            @Override
            public void onTaskCompleted(CMCCoin[] cmcCoinList) {
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                currencyItemList.clear();
                currencyItemMap.clear();
                try {
                    for (CMCCoin coin : cmcCoinList) {
                        currencyItemList.add(coin);
                        currencyItemMap.put(coin.getSymbol(), coin);
                    }
                    adapter.setCurrencyList(currencyItemList);
                    adapter.notifyDataSetChanged();
                    currencyRecyclerView.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_currency_list, container, false);
//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setHasOptionsMenu(true);
        this.db = DatabaseHelperSingleton.getInstance(getActivity());
        // Setup currency list
        currencyRecyclerView = (RecyclerView) rootView.findViewById(R.id.currency_list_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        currencyItemMap = new Hashtable<>();
        adapter = new CurrencyListAdapter(currencyItemList, db, (AppCompatActivity) getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), CurrencyDetailsTabsActivity.class);
                intent.putExtra(CurrencyListTabsActivity.SYMBOL, currencyItemList.get(position).getSymbol());
                getActivity().startActivity(intent);
            }
        });

        // Setup swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.currency_list_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getCurrencyList();
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_button:
                getActivity().startActivity(new Intent(getActivity(), NewsListActivity.class));
                return true;
            case R.id.currency_refresh_button:
                onRefresh();
                return true;
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        query = query.toLowerCase();
        final List<CMCCoin> filteredList = new ArrayList<>();
        for (CMCCoin coin : currencyItemList) {
            if (coin.getSymbol().toLowerCase().contains(query) || coin.getName().toLowerCase().contains(query)) {
                filteredList.add(coin);
            }
        }
        adapter = new CurrencyListAdapter(filteredList, db, (AppCompatActivity) getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), CurrencyDetailsTabsActivity.class);
                intent.putExtra(CurrencyListTabsActivity.SYMBOL, filteredList.get(position).getSymbol());
                getActivity().startActivity(intent);
            }
        });
        currencyRecyclerView.setAdapter(adapter);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem newsButton = menu.findItem(R.id.news_button);
        final MenuItem refreshButton = menu.findItem(R.id.currency_refresh_button);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    newsButton.setVisible(false);
                    refreshButton.setVisible(false);
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
                } else {
                    getActivity().invalidateOptionsMenu();
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                }
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
