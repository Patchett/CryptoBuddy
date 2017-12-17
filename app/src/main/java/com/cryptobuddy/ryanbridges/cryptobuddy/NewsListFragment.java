package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Ryan on 11/21/2017.
 */

public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String NEWS_API_KEY = BuildConfig.API_KEY;
    public final static String BTC_NEWS_URL_TEMPLATE = "http://eventregistry.org/json/article?query=%7B\"%24query\"%3A%7B\"%24and\"%3A%5B%7B\"conceptUri\"%3A%7B\"%24and\"%3A%5B\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEthereum\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCryptocurrency\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBitcoin\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FLitecoin\"%5D%7D%7D%2C%7B\"lang\"%3A\"eng\"%7D%5D%7D%7D&action=getArticles&resultType=articles&articlesSortBy=date&articlesCount=20&apiKey=";
    public final static String BTC_NEWS_URL = BTC_NEWS_URL_TEMPLATE + NEWS_API_KEY;
    private NewsListAdapter adapter;
    private List<NewsItem> newsItemList;
    private RecyclerView recyclerView;

    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public NewsListFragment() {
    }


    public static NewsListFragment newInstance(String tabName) {
        return new NewsListFragment();
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
                Log.e("ERROR", "Server Error: " + e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news_list_fragment, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_recycler);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.newsListRecyclerView);
        HorizontalDividerItemDecoration divider = new HorizontalDividerItemDecoration.Builder(getActivity()).build();
        recyclerView.addItemDecoration(divider);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        newsItemList = new ArrayList<>();
        adapter = new NewsListAdapter(newsItemList, new CustomItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent browserIntent = new Intent(getActivity(), WebViewActivity.class);
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
        return rootView;
    }

}
