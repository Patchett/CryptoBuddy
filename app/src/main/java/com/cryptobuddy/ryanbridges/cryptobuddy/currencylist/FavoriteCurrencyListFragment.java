package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.cryptobuddy.ryanbridges.cryptobuddy.currencydetails.CurrencyDetailsTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.currencydetails.chartandtable.GraphFragment;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCQuickSearch;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.NewsListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Ryan on 1/21/2018.
 */

public class FavoriteCurrencyListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHelperSingleton db;
    private RecyclerView currencyRecyclerView;
    private FavsCurrencyListAdapter adapter;
    private ArrayList<CMCCoin> currencyItemFavsList = new ArrayList<>();
    private AllCoinsListUpdater favsUpdateCallback;
    private AppCompatActivity mContext;
    private HashMap<String, Integer> slugToIDMap = new HashMap<>();

    public interface AllCoinsListUpdater {
        void allCoinsModifyFavorites(CMCCoin coin);
    }

    public FavoriteCurrencyListFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FavoriteCurrencyListFragment newInstance() {
        return new FavoriteCurrencyListFragment();
    }

    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinMarketCapService.getAllCoins(getActivity(), new afterTaskCompletion<CMCCoin[]>() {
            @Override
            public void onTaskCompleted(CMCCoin[] cmcCoinList) {
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                currencyItemFavsList.clear();
                CoinFavoritesStructures favs = db.getFavorites();
                try {
                    for (CMCCoin coin : cmcCoinList) {
                        if (favs.favoritesMap.get(coin.getSymbol()) != null) {
                            currencyItemFavsList.add(coin);
                        }
                    }
                    getQuickSearch();
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

    public void getQuickSearch() {
        CoinMarketCapService.getCMCQuickSearch(mContext, new afterTaskCompletion<CMCQuickSearch[]>() {
            @Override
            public void onTaskCompleted(CMCQuickSearch[] quickSearchNodeList) {
                slugToIDMap = new HashMap<>();
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                for (CMCQuickSearch node : quickSearchNodeList) {
                    slugToIDMap.put(node.getSlug(), node.getId());
                }
                for (CMCCoin coin : currencyItemFavsList) {
                    coin.setQuickSearchID(slugToIDMap.get(coin.getId()));
                }
                adapter.setCurrencyList(currencyItemFavsList);
                adapter.notifyDataSetChanged();
                currencyRecyclerView.setAdapter(adapter);
                currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                swipeRefreshLayout.setRefreshing(false);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        favsUpdateCallback = (AllCoinsListUpdater) activity;
        mContext = (AppCompatActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorite_currency_list, container, false);
        setHasOptionsMenu(true);
        this.db = DatabaseHelperSingleton.getInstance(getActivity());
        currencyRecyclerView = (RecyclerView) rootView.findViewById(R.id.currency_favs_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemFavsList = new ArrayList<>();
        adapter = new FavsCurrencyListAdapter(favsUpdateCallback, currencyItemFavsList, db, (AppCompatActivity) getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), CurrencyDetailsTabsActivity.class);
                intent.putExtra(GraphFragment.ARG_SYMBOL, currencyItemFavsList.get(position).getSymbol());
                intent.putExtra(GraphFragment.ARG_ID, currencyItemFavsList.get(position).getId());
                intent.putExtra(GraphFragment.COIN_OBJECT, currencyItemFavsList.get(position));
                getActivity().startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.currency_favs_swipe_refresh);
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
    public void onResume() {
        super.onResume();
        if (rootView != null) { // Hide keyboard when we enter this tab
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
        mContext.getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.favorite_currency_list_tab_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_button_favs_list:
                mContext.startActivity(new Intent(mContext, NewsListActivity.class));
                return true;
            case R.id.currency_refresh_button_favs_list:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }

    public void removeFavorite(CMCCoin coin) {
        ArrayList<CMCCoin> currentFavs = adapter.getCurrencyList();
        Iterator<CMCCoin> currFavsIterator = currentFavs.iterator();
        while (currFavsIterator.hasNext()) {
            CMCCoin currCoin = currFavsIterator.next();
            if (currCoin.getId().equals(coin.getId())) {
                currFavsIterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    public void addFavorite(CMCCoin coin) {
        currencyItemFavsList.add(0, coin);
        adapter.notifyDataSetChanged();
    }

}
