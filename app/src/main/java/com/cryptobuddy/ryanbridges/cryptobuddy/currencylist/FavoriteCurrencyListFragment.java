package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.CurrencyDetailsTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Hashtable;
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
    private ArrayList<CMCCoin> allCoinsList = new ArrayList<>();
    private Hashtable<String, CMCCoin> allCoinsMap = new Hashtable<>();
    private Hashtable<String, CMCCoin> currencyItemMap = new Hashtable<>();
    private AllCoinsListUpdater favsUpdateCallback;

    public interface AllCoinsListUpdater {
        public void allCoinsRemoveFavorite(CMCCoin coin);
        public void allCoinsAddFavorite(CMCCoin coin);
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
                currencyItemMap.clear();
                allCoinsList.clear();
                allCoinsMap.clear();
                CoinFavoritesStructures favs = db.getFavorites();
                try {
                    for (CMCCoin coin : cmcCoinList) {
                        allCoinsList.add(coin);
                        allCoinsMap.put(coin.getSymbol(), coin);
                        if (favs.favoritesMap.get(coin.getSymbol()) != null) {
                            currencyItemFavsList.add(coin);
                            currencyItemMap.put(coin.getSymbol(), coin);
                        }
                    }
                    adapter.setCurrencyList(currencyItemFavsList);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        favsUpdateCallback = (AllCoinsListUpdater) activity;
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
        allCoinsList = new ArrayList<>();
        currencyItemMap = new Hashtable<>();
        allCoinsMap = new Hashtable<>();
        adapter = new FavsCurrencyListAdapter(currencyItemFavsList, db, (AppCompatActivity) getActivity(), new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), CurrencyDetailsTabsActivity.class);
                intent.putExtra(CurrencyListTabsActivity.SYMBOL, currencyItemFavsList.get(position).getSymbol());
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

    public void updateFavs() {
        CoinFavoritesStructures dbFavs = db.getFavorites();
        ArrayList<CMCCoin> currentFavs = adapter.getCurrencyList();
        // Remove stale favs
        Iterator<CMCCoin> currFavsIterator = currentFavs.iterator();
        while (currFavsIterator.hasNext()) {
            CMCCoin currCoin = currFavsIterator.next();
            if (dbFavs.favoritesMap.get(currCoin.getSymbol()) == null) { // Check if the fav is already not in the list
                // Remove the fav
                currencyItemMap.remove(currCoin.getSymbol());
                currFavsIterator.remove();
            }
        }
        // Add new favorites
        Iterator<String> dbIterator = dbFavs.favoriteList.iterator();
        while(dbIterator.hasNext()) {
            String currSymbol = dbIterator.next();
            if (currencyItemMap.get(currSymbol) == null) {
                CMCCoin newCoin = allCoinsMap.get(currSymbol);
                currencyItemMap.put(currSymbol, newCoin);
                currentFavs.add(0, newCoin);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rootView != null) { // Hide keyboard when we enter this tab
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
        new UpdateFavoritesTask(adapter, currencyItemMap).execute();
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }


    class UpdateFavoritesTask extends AsyncTask<Void, Void, Void> {

        private CoinFavoritesStructures dbFavs;
        private ArrayList<CMCCoin> currentFavs;
        FavsCurrencyListAdapter adapter;
        Hashtable<String, CMCCoin> currencyItemMap;

        public UpdateFavoritesTask(FavsCurrencyListAdapter adapter, Hashtable<String, CMCCoin> currencyItemMap) {
            this.adapter = adapter;
            this.currencyItemMap = currencyItemMap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dbFavs = db.getFavorites();
            this.currentFavs = adapter.getCurrencyList();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            this.adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Iterator<CMCCoin> currFavsIterator = currentFavs.iterator();
            while (currFavsIterator.hasNext()) {
                CMCCoin currCoin = currFavsIterator.next();
                if (dbFavs.favoritesMap.get(currCoin.getSymbol()) == null) { // Check if the fav is already not in the list
                    // Remove the fav
                    this.currencyItemMap.remove(currCoin.getSymbol());
                    currFavsIterator.remove();
                }
            }
            // Add new favorites
//            Iterator<String> dbIterator = dbFavs.favoriteList.iterator();
//            while(dbIterator.hasNext()) {
//                String currSymbol = dbIterator.next();
//                if (this.currencyItemMap.get(currSymbol) == null) {
//                    CMCCoin newCoin = allCoinsMap.get(currSymbol);
//                    this.currencyItemMap.put(currSymbol, newCoin);
//                    currentFavs.add(0, newCoin);
//                }
//            }
            return null;
        }
    }

    public FavsCurrencyListAdapter getAdapter() {
        return this.adapter;
    }
}

