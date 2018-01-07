package com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.ChartAndPrice.CurrencyTabsActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.CoinFavoritesStructures;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.DatabaseHelperSingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.News.NewsListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.VolleySingleton;
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
//    private String HOME_CURRENCY_LIST_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC,ETH,LTC,ETC,XRP,XMR,DASH,BCH,BTG,XLM,XVG,XRB,SNM,LSK,SALT,XP,ADA,STEEM,ENG,EVX,UFR,CND,DBC,LINK,KCS&tsyms=USD";
    public final static String SYMBOL = "SYMBOL";
    public static String baseImageURL;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private CurrencyListAdapter adapter;
    private List<CurrencyListItem> currencyItemList;
    private Hashtable<String, CoinMetadata> coinMetadataTable;
    private AppCompatActivity me;
    private DatabaseHelperSingleton db;
    public static final String ALL_COINS_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        this.me = this;
        this.db = DatabaseHelperSingleton.getInstance(this);
        CoinFavoritesStructures coinFavs = db.getFavorites();
        formattedCurrencyListURL = String.format(HOME_CURRENCY_LIST_URL, android.text.TextUtils.join(",", coinFavs.favoriteList));
        // Setup currency list
        currencyRecyclerView = (RecyclerView) findViewById(R.id.currency_list_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        coinMetadataTable = new Hashtable<>();
        adapter = new CurrencyListAdapter(currencyItemList, getString(R.string.negative_percent_change_format), getString(R.string.positive_percent_change_format), getString(R.string.price_format), getResources().getColor(R.color.percentPositiveGreen), getResources().getColor(R.color.percentNegativeRed), me, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(me, CurrencyTabsActivity.class);
                intent.putExtra(SYMBOL, currencyItemList.get(position).symbol);
                startActivity(intent);
                Toast.makeText(CurrencyListActivity.this, "You selected: " + currencyItemList.get(position).symbol, Toast.LENGTH_LONG).show();
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
                getAllCoinsList();
            }
        });
    }

    public void getCurrencyList() {
        swipeRefreshLayout.setRefreshing(true);
        Log.d("I", "inside of getCurrencyList()");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, formattedCurrencyListURL, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("I", "Currency List: " + response.toString());
                    currencyItemList.clear();
                    try {
                        JSONObject rawResponse = response.getJSONObject("RAW");
                        for(Iterator<String> iter = rawResponse.keys();iter.hasNext();) {
                            String currency = iter.next();
                            Log.d("I", "currency " + currency);
                            try {
                                JSONObject currencyDetails = rawResponse.getJSONObject(currency).getJSONObject("USD");
                                Double changePCT24hr = currencyDetails.getDouble("CHANGEPCT24HOUR");
                                Double change24hr = currencyDetails.getDouble("CHANGE24HOUR");
                                Double currPrice = currencyDetails.getDouble("PRICE");
                                currencyItemList.add(new CurrencyListItem(currency, currPrice, change24hr, changePCT24hr, coinMetadataTable.get(currency).imageURL, coinMetadataTable.get(currency).fullName));
                                Log.d("I", "PRICE: " + currPrice);
                                Log.d("I", "CHANGE24HOUR: " + change24hr);
                                Log.d("I", "CHANGEPCT24HOUR " + changePCT24hr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        currencyRecyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeRefreshLayout.setRefreshing(false);
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
        Log.d("I", "inside of getAllCoinsList()");
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ALL_COINS_LIST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            baseImageURL = response.getString("BaseImageUrl");
                            JSONObject data = response.getJSONObject("Data");
                            Log.d("I", "Data from getAllCoinsList(): " + data);
                            for (Iterator<String> iter = data.keys(); iter.hasNext(); ) {
                                String currency = iter.next();
                                try {
                                    JSONObject currencyDetails = data.getJSONObject(currency);
                                    String imageURL = currencyDetails.getString("ImageUrl");
                                    String fullName = currencyDetails.getString("FullName");
                                    String symbol = currencyDetails.getString("Symbol");
                                    coinMetadataTable.put(symbol, new CoinMetadata(imageURL, fullName, symbol));
                                    Log.d("I", "ImageUrl: " + imageURL);
                                    Log.d("I", "FullName: " + fullName);
                                    Log.d("I", "Symbol " + symbol);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            getCurrencyList();
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getAllCoinsList();
    }
}
