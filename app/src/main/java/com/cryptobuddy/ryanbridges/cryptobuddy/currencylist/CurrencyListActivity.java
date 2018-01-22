package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.CurrencyTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.coinfavorites.CoinMetadata;
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



public class CurrencyListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    public final static String SYMBOL = "SYMBOL";
    public static String baseImageURL;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private CurrencyListAdapter adapter;
    private List<CMCCoin> currencyItemList;
    private Hashtable<String, CMCCoin> currencyItemMap;
    private Hashtable<String, CoinMetadata> coinMetadataTable;
    private AppCompatActivity me;
    private DatabaseHelperSingleton db;
    private MenuItem searchItem;
    private SearchView searchView;
    public final static String IMAGE_URL_FORMAT = "https://files.coinmarketcap.com/static/img/coins/64x64/%s.png";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        this.me = this;
        this.db = DatabaseHelperSingleton.getInstance(this);
        // Setup currency list
        currencyRecyclerView = (RecyclerView) findViewById(R.id.currency_list_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        coinMetadataTable = new Hashtable<>();
        currencyItemMap = new Hashtable<>();
        adapter = new CurrencyListAdapter(currencyItemList, currencyItemMap, db, me, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(me, CurrencyTabsActivity.class);
                intent.putExtra(SYMBOL, currencyItemList.get(position).getSymbol());
                startActivity(intent);
            }
        });

        // Setup swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.currency_list_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getCurrencyList();
            }
        });
    }

    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinMarketCapService.getAllCoins(this, new afterTaskCompletion<CMCCoin[]>() {
            @Override
            public void onTaskCompleted(CMCCoin[] cmcCoinList) {
                Parcelable recyclerViewState;
                recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                currencyItemList.clear();
                currencyItemMap.clear();
                try {
                    for (CMCCoin coin : cmcCoinList) {
                        Log.d("I", coin.getSymbol());
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
                Toast.makeText(CurrencyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }

    @Override
    public void onResume(){
        super.onResume();
//        this.onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                    getSupportActionBar().setTitle("");
                } else {
                    invalidateOptionsMenu();
                    getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        adapter = new CurrencyListAdapter(filteredList, currencyItemMap, db, me, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(me, CurrencyTabsActivity.class);
                intent.putExtra(SYMBOL, filteredList.get(position).getSymbol());
                startActivity(intent);
            }
        });
        currencyRecyclerView.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_button:
                startActivity(new Intent(this, NewsListActivity.class));
                return true;
            case R.id.currency_refresh_button:
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }
}
