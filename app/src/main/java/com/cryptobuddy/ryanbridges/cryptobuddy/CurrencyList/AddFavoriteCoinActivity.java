package com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.VolleySingleton;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList.CurrencyListActivity.ALL_COINS_LIST_URL;

/**
 * Created by Ryan on 12/31/2017.
 */

public class AddFavoriteCoinActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView coinRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<CoinMetadata> coinList;
    private AddFavoriteCoinListAdapter adapter;
    private AppCompatActivity me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorite_coin);
        getSupportActionBar().setTitle("Favorite Coins");
        coinList = new ArrayList<>();
        me = this;
        coinRecyclerView = (RecyclerView) findViewById(R.id.coin_favs_recycler_view);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        coinRecyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        coinRecyclerView.setLayoutManager(llm);

        adapter = new AddFavoriteCoinListAdapter(coinList, me, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Toast.makeText(AddFavoriteCoinActivity.this, "You selected: " + coinList.get(position).symbol, Toast.LENGTH_LONG).show();

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
    }

    public void getAllCoinsList() {
        Log.d("I", "inside of in addfavoritecoinactivity()");
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, ALL_COINS_LIST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("Data");
                            Log.d("I", "Data in getAllCoinsList addfavorite: " + data);
                            for (Iterator<String> iter = data.keys(); iter.hasNext(); ) {
                                String currency = iter.next();
                                try {
                                    JSONObject currencyDetails = data.getJSONObject(currency);
                                    Log.d("I", "currencyDetails in getAllCoinsList addfavorite: " + currencyDetails);
                                    String fullName = currencyDetails.getString("FullName");
                                    String symbol = currencyDetails.getString("Symbol");
                                    coinList.add(new CoinMetadata("", fullName, symbol));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            adapter.notifyDataSetChanged();
                            Log.d("I", "coinListSize: " + coinList.size());
                            coinRecyclerView.setAdapter(adapter);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                Toast.makeText(AddFavoriteCoinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    public void onRefresh() {
        this.getAllCoinsList();
    }

}
