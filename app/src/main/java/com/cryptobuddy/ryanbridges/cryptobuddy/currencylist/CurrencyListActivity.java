package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.DatabaseHelperSingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.VolleySingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice.CurrencyTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.coinfavorites.AddFavoriteCoinActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.coinfavorites.CoinMetadata;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinList;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.DataNode;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.NewsListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinService;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;



public class CurrencyListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String HOME_CURRENCY_LIST_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=%s&tsyms=USD";
    private String formattedCurrencyListURL;
    public final static String SYMBOL = "SYMBOL";
    public static String baseImageURL;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private CurrencyListAdapter adapter;
    private List<CurrencyListItem> currencyItemList;
    private Hashtable<String, CurrencyListItem> currencyItemMap;
    private Hashtable<String, CoinMetadata> coinMetadataTable;
    private AppCompatActivity me;
    private DatabaseHelperSingleton db;
    private ItemTouchHelper mItemTouchHelper;


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
                intent.putExtra(SYMBOL, currencyItemList.get(position).symbol);
                startActivity(intent);
                Toast.makeText(CurrencyListActivity.this, "You selected: " + currencyItemList.get(position).symbol, Toast.LENGTH_LONG).show();
            }
        });
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(currencyRecyclerView);

        // Setup swipe refresh layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.currency_list_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                getAllCoinsList();
            }
        });
    }

    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinFavoritesStructures coinFavs = db.getFavorites();
        formattedCurrencyListURL = String.format(HOME_CURRENCY_LIST_URL, android.text.TextUtils.join(",", coinFavs.favoriteList));
        Log.d("I", "formattedCurrencyListURL: %s" + formattedCurrencyListURL);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, formattedCurrencyListURL, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Parcelable recyclerViewState;
                    recyclerViewState = currencyRecyclerView.getLayoutManager().onSaveInstanceState();
                    currencyItemList.clear();
                    currencyItemMap.clear();
                    try {
                        JSONObject rawResponse = response.getJSONObject("RAW");
                        for(Iterator<String> iter = rawResponse.keys();iter.hasNext();) {
                            String currency = iter.next();
                            try {
                                JSONObject currencyDetails = rawResponse.getJSONObject(currency).getJSONObject("USD");
                                Double changePCT24hr = currencyDetails.getDouble("CHANGEPCT24HOUR");
                                Double change24hr = currencyDetails.getDouble("CHANGE24HOUR");
                                Double currPrice = currencyDetails.getDouble("PRICE");
                                Double mktCap = currencyDetails.getDouble("MKTCAP");
                                Double totalvolume24H = currencyDetails.getDouble("TOTALVOLUME24H");
                                CurrencyListItem newItem = new CurrencyListItem(currency, currPrice, change24hr, changePCT24hr,
                                        coinMetadataTable.get(currency).imageURL, coinMetadataTable.get(currency).fullName, mktCap, totalvolume24H);
                                currencyItemList.add(newItem);
                                currencyItemMap.put(currency, newItem);
                            } catch (JSONException e) {
                                continue;
                            }
                        }
                        adapter.setCurrencyList(currencyItemList);
                        adapter.notifyDataSetChanged();
                        currencyRecyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    currencyRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                }
    }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                Toast.makeText(CurrencyListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    public void getAllCoinsList() {
        swipeRefreshLayout.setRefreshing(true);
        CoinService.getAllCoins(this, new afterTaskCompletion<CoinList>() {
            @Override
            public void onTaskCompleted(CoinList coinList) {

                try {
                    baseImageURL = coinList.getBaseImageUrl();
                    for(DataNode data : coinList.getData().getDataList()){
                        coinMetadataTable.put(data.getSymbol(), new CoinMetadata(data.getImageUrl(), data.getFullName(), data.getSymbol()));
                    }
                    getCurrencyList();
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
        this.onRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_button:
                startActivity(new Intent(this, NewsListActivity.class));
                return true;
            case R.id.add_currency_button:
                startActivity(new Intent(this, AddFavoriteCoinActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            case R.id.currency_refresh_button:
                onRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getAllCoinsList();
    }
}
