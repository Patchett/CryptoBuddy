package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;

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
    private CurrencyListAdapterBase adapter;
    private List<CMCCoin> currencyItemList;
    private Hashtable<String, CMCCoin> currencyItemMap;
    private DatabaseHelperSingleton db;
    private MenuItem searchItem;
    private SearchView searchView;
    private View rootView;
    private boolean searchViewFocus = false;
    private Context mContext;

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
        CoinMarketCapService.getAllCoins(mContext, new afterTaskCompletion<CMCCoin[]>() {
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
        this.db = DatabaseHelperSingleton.getInstance(mContext);
        // Setup currency list
        currencyRecyclerView = (RecyclerView) rootView.findViewById(R.id.currency_list_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(mContext).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        currencyItemMap = new Hashtable<>();
        adapter = new CurrencyListAdapterBase(currencyItemList, db, (AppCompatActivity) mContext, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(mContext, CurrencyDetailsTabsActivity.class);
                intent.putExtra(CurrencyListTabsActivity.SYMBOL, currencyItemList.get(position).getSymbol());
                mContext.startActivity(intent);
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
                mContext.startActivity(new Intent(mContext, NewsListActivity.class));
                return true;
            case R.id.currency_refresh_button:
                onRefresh();
                return true;
            default:
                Log.d("I", "Inside of default onOptionsItemSelected");
                return super.onOptionsItemSelected(item);
        }
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
        adapter = new CurrencyListAdapterBase(filteredList, db, (AppCompatActivity) mContext, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(mContext, CurrencyDetailsTabsActivity.class);
                intent.putExtra(CurrencyListTabsActivity.SYMBOL, filteredList.get(position).getSymbol());
                mContext.startActivity(intent);
            }
        });
        currencyRecyclerView.setAdapter(adapter);
        return true;
    }

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d("I", "Inside of onPrepareOptionsMenu");

        if (searchView != null && !searchView.isIconified()) {
            Log.d("I", "Inside of onPrepareOptionsMenu if statement");
            searchView.requestFocus();
            showInputMethod(rootView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem newsButton = menu.findItem(R.id.news_button);
        final MenuItem refreshButton = menu.findItem(R.id.currency_refresh_button);
        Log.d("I", "Inside of onCreateOptionsMenu");
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
//         Detect SearchView icon clicks
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemsVisibility(menu, searchItem, false);
            }
        });
        // Detect SearchView close
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getActivity().invalidateOptionsMenu();
                ((AppCompatActivity)mContext).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                return false;
            }
        });
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    newsButton.setVisible(false);
//                    refreshButton.setVisible(false);
//                    showInputMethod(rootView.findFocus());
//                    ((AppCompatActivity)mContext).getSupportActionBar().setTitle("");
//                } else {
//                    getActivity().invalidateOptionsMenu();
//                    ((AppCompatActivity)mContext).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
//                }
//            }
//        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != exception) item.setVisible(visible);
        }
        if (visible == false) {
            ((AppCompatActivity)mContext).getSupportActionBar().setTitle("");
        } else {
            ((AppCompatActivity)mContext).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        }
    }


}
