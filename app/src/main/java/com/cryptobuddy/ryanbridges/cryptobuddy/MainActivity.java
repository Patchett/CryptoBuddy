package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String NEWS_API_KEY = BuildConfig.API_KEY;
    public final static String BTC_NEWS_URL_TEMPLATE = "http://eventregistry.org/json/article?query=%7B\"%24query\"%3A%7B\"%24and\"%3A%5B%7B\"conceptUri\"%3A%7B\"%24and\"%3A%5B\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEthereum\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCryptocurrency\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBitcoin\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FLitecoin\"%5D%7D%7D%2C%7B\"lang\"%3A\"eng\"%7D%5D%7D%7D&action=getArticles&resultType=articles&articlesSortBy=date&articlesCount=20&apiKey=";
    private String homeCurrencyListURL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC,ETH,LTC,ETC,XRP,XMR,DASH,BCH,BTG&tsyms=USD";
    public final static String BTC_NEWS_URL = BTC_NEWS_URL_TEMPLATE + NEWS_API_KEY;
    private String TAG = MainActivity.class.getSimpleName();
    public final static String SYMBOL = "SYMBOL";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView currencyRecyclerView;
    private CurrencyListAdapter adapter;
    private List<CurrencyListItem> currencyItemList;
    private AppCompatActivity me;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        me = this;
        // Setup currency list
        currencyRecyclerView = (RecyclerView) findViewById(R.id.currency_list_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        currencyRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        currencyRecyclerView.setLayoutManager(llm);
        currencyItemList = new ArrayList<>();
        adapter = new CurrencyListAdapter(currencyItemList, getString(R.string.negative_percent_change_format), getString(R.string.positive_percent_change_format), getString(R.string.price_format), getResources().getColor(R.color.percentPositiveGreen), getResources().getColor(R.color.percentNegativeRed), new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(me, CurrencyTabsActivity.class);
                intent.putExtra(SYMBOL, currencyItemList.get(position).symbol);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "You selected: " + currencyItemList.get(position).symbol, Toast.LENGTH_LONG).show();

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
        Log.d("I", "inside of getCurrencyList()");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, homeCurrencyListURL, null,
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
                                currencyItemList.add(new CurrencyListItem(currency, currPrice, change24hr, changePCT24hr));
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
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onRefresh() {
        getCurrencyList();
    }
}
