package com.cryptobuddy.ryanbridges.cryptobuddy.News;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.BuildConfig;
import com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList.CurrencyListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.VolleySingleton;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;

/**
 * Created by Ryan on 12/28/2017.
 */

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String NEWS_API_KEY = BuildConfig.API_KEY;
    public final static String BTC_NEWS_URL = "https://min-api.cryptocompare.com/data/news/";
    private NewsListAdapter adapter;
    private List<NewsItem> newsItemList;
    private RecyclerView recyclerView;
    private AppCompatActivity mActivity;
    private SwipeRefreshLayout swipeRefreshLayout;


    public void getNewsRequest() {
        swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BTC_NEWS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
//                        Log.d("I", "NEWS: " + response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                final String articleURL = row.getString("url");
                                String articleTitle = row.getString("title");
                                String articleBody = row.getString("body");
                                String imageURL = row.getString("imageurl");
                                String sourceName = row.getJSONObject("source_info").getString("name");
                                long publishedOn = row.getLong("published_on");
                                newsItemList.add(new NewsItem(articleTitle, articleURL, articleBody, imageURL, sourceName, publishedOn));
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
//                Log.e("ERROR", "Server Error: " + e.getMessage());
//                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.News));
        mActivity = this;
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_recycler);
        recyclerView = (RecyclerView) findViewById(R.id.newsListRecyclerView);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        newsItemList = new ArrayList<>();
        adapter = new NewsListAdapter(newsItemList, this, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent browserIntent = new Intent(mActivity, WebViewActivity.class);
                browserIntent.putExtra("url", newsItemList.get(position).articleURL);
                startActivity(browserIntent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getNewsRequest();
                                    }
                                }
        );
    }

    @Override
    public void onRefresh() {
        getNewsRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.home_button:
                startActivity(new Intent(this, CurrencyListActivity.class));
                finish();
                return true;
            case R.id.news_refresh_button:
                onRefresh();
                return true;
        }
        finish();
        return true;
    }
}
