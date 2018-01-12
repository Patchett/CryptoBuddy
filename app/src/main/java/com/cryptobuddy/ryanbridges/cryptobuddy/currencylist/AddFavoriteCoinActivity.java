package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.cryptobuddy.ryanbridges.cryptobuddy.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.DatabaseHelperSingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinList;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.DataNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinService;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.currencylist.CurrencyListActivity.ALL_COINS_LIST_URL;

/**
 * Created by Ryan on 12/31/2017.
 */

public class AddFavoriteCoinActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView coinRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<CoinMetadata> allCoinList;
    private AddFavoriteCoinListAdapter adapter;
    private AppCompatActivity me;
    private SearchView searchView;
    private DatabaseHelperSingleton db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_coin);
        getSupportActionBar().setTitle("Favorite Coins");
        allCoinList = new ArrayList<>();
        me = this;
        coinRecyclerView = (RecyclerView) findViewById(R.id.coin_favs_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        coinRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        coinRecyclerView.setLayoutManager(llm);
        this.db = DatabaseHelperSingleton.getInstance(this);
        adapter = new AddFavoriteCoinListAdapter(allCoinList, me, db, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                CoinFavoritesStructures favs = db.getFavorites();
                CoinMetadata item = allCoinList.get(position);
                if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet. Add it.
                    favs.favoritesMap.put(item.symbol, item.symbol);
                    favs.favoriteList.add(item.symbol);
                } else { // Coin is already a favorite, remove it
                    favs.favoritesMap.remove(item.symbol);
                    favs.favoriteList.remove(item.symbol);
                }
                db.saveCoinFavorites(favs);
            }
        });

        // Setup swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.coin_favs_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getAllCoinsList();
            }
        });

        searchView = (SearchView) findViewById(R.id.search_view_coin_favorites);
        searchView.setQueryHint("Search Coins");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                final List<CoinMetadata> filteredList = new ArrayList<>();
                for (int i = 0; i < allCoinList.size(); i++) {
                    CoinMetadata currCoin = allCoinList.get(i);
                    if (currCoin.fullName.toLowerCase().contains(newText)) {
                        filteredList.add(currCoin);
                    }
                }
                adapter = new AddFavoriteCoinListAdapter(filteredList, me, db, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        CoinFavoritesStructures favs = db.getFavorites();
                        CoinMetadata item = filteredList.get(position);
                        if (favs.favoritesMap.get(item.symbol) == null) { // Coin is not a favorite yet. Add it.
                            favs.favoritesMap.put(item.symbol, item.symbol);
                            favs.favoriteList.add(item.symbol);
                        } else { // Coin is already a favorite, remove it
                            favs.favoritesMap.remove(item.symbol);
                            favs.favoriteList.remove(item.symbol);
                        }
                        db.saveCoinFavorites(favs);
                    }
                });
                coinRecyclerView.setAdapter(adapter);
                return true;
            }
        });
        searchView.setIconified(false);

    }

    public void getAllCoinsList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinService.getAllCoins(this, new afterTaskCompletion<CoinList>() {
            @Override
            public void onTaskCompleted(CoinList coinList) {
                try {
                    CurrencyListActivity.baseImageURL = coinList.getBaseImageUrl();
                    for(DataNode data : coinList.getData().getDataList()){
                        allCoinList.add(new CoinMetadata(data.getImageUrl(), data.getFullName(), data.getSymbol()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
                Log.d("I", "coinListSize: " + allCoinList.size());
                coinRecyclerView.setAdapter(adapter);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                Toast.makeText(AddFavoriteCoinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onRefresh() {
        this.getAllCoinsList();
    }

}
