package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public final static String BTC_NEWS_URL = "http://eventregistry.org/json/article?query=%7B\"%24query\"%3A%7B\"%24and\"%3A%5B%7B\"conceptUri\"%3A%7B\"%24and\"%3A%5B\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEthereum\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCryptocurrency\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBitcoin\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FLitecoin\"%5D%7D%7D%2C%7B\"lang\"%3A\"eng\"%7D%5D%7D%7D&action=getArticles&resultType=articles&articlesSortBy=date&articlesCount=20&apiKey=0a4f710c-cac5-4e7a-8db2-f9a68c579353";
    public final Activity thisActivity = this;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(this).build();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.newsListRecyclerView);
        mRecyclerView.addItemDecoration(divider);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(getNewsRequest());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public JsonObjectRequest getNewsRequest() {
        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.newsListRecyclerView);

        return new JsonObjectRequest(Request.Method.GET, BTC_NEWS_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("I", "NEWS: " + response.toString());
                        final List<NewsItem> newsItemList = new ArrayList<>();
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NewsListAdapter adapter = new NewsListAdapter(newsItemList, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(int position, View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItemList.get(position).articleURL));
                                startActivity(browserIntent);

                            }
                        });
                        mRecyclerView.setAdapter(adapter);
                        LinearLayoutManager llm = new LinearLayoutManager(thisActivity);
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(llm);
                        mRecyclerView.setHasFixedSize(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
    }

}
