package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final static String BTC_NEWS_URL = "http://eventregistry.org/json/article?query=%7B\"%24query\"%3A%7B\"%24and\"%3A%5B%7B\"conceptUri\"%3A%7B\"%24and\"%3A%5B\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEthereum\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCryptocurrency\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBitcoin\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FLitecoin\"%5D%7D%7D%2C%7B\"lang\"%3A\"eng\"%7D%5D%7D%7D&action=getArticles&resultType=articles&articlesSortBy=date&articlesCount=20&apiKey=0a4f710c-cac5-4e7a-8db2-f9a68c579353";
    private String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private NewsListAdapter adapter;
    private List<NewsItem> newsItemList;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_recycler);
        newsItemList = new ArrayList<>();
        adapter = new NewsListAdapter(newsItemList, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent browserIntent = new Intent(MainActivity.this, WebViewActivity.class);
                browserIntent.putExtra("url", newsItemList.get(position).articleURL);
                startActivity(browserIntent);
            }
        });
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        recyclerView = (RecyclerView) findViewById(R.id.newsListRecyclerView);
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
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

    public void getNewsRequest() {
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, BTC_NEWS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("I", "NEWS: " + response.toString());
                        try {
                            JSONArray articles = response.getJSONObject("articles").getJSONArray("results");
                            Log.d("I", "NEWS_ARTICLES: " + articles);
                            for (int i = 0; i < articles.length(); i++) {
                                JSONObject row = articles.getJSONObject(i);
                                String articleTitle = row.getString("title");
                                final String articleURL = row.getString("url");
                                String articleBody = row.getString("body");
                                newsItemList.add(new NewsItem(articleTitle, articleURL, articleBody));
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
                Log.e(TAG, "Server Error: " + e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }
}
