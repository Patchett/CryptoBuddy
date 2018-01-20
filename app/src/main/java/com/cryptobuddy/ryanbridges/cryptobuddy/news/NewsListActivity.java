package com.cryptobuddy.ryanbridges.cryptobuddy.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.currencylist.CurrencyListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.News;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.NewsService;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.VolleySingleton;
import com.grizzly.rest.Model.RestResults;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;

/**
 * Created by Ryan on 12/28/2017.
 */

public class NewsListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private NewsListAdapter adapter;
    private List<NewsItem> newsItemList;
    private RecyclerView recyclerView;
    private AppCompatActivity mActivity;
    private SwipeRefreshLayout swipeRefreshLayout;


    public void getNewsRequest() {
        swipeRefreshLayout.setRefreshing(true);
        NewsService.getNewsVolley(new VolleySingleton.OnVolleyRequest() {
            @Override
            public void parse(JSONArray jsonArray) {
                Parcelable recyclerViewState;
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject row = jsonArray.getJSONObject(i);
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
                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
            }

            @Override
            public void onParseError() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    
    public void getNewsObservable(int whatToDo){

        swipeRefreshLayout.setRefreshing(true);
        //Example of framework isolation by using observables
        //An standard Rx Action.
        Action1<News[]> subscriber = new Action1<News[]>() {
            @Override
            public void call(News[] newsRestResults) {
                if(newsRestResults!=null && newsRestResults.length>0){
                    Parcelable recyclerViewState;
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    for(News news: newsRestResults){
                        newsItemList.add(new NewsItem(news.getTitle(),
                                news.getUrl(), news.getBody(),
                                news.getImageurl(), news.getSource(),
                                news.getPublishedOn()));
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };

        switch (whatToDo){
            case 1:
                //Wrapped observable call
                NewsService.getObservableNews(this, true, new Action1<RestResults<News[]>>() {
                    @Override
                    public void call(RestResults<News[]> newsRestResults) {
                        Parcelable recyclerViewState;
                        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                        if(newsRestResults.isSuccessful()){
                            for(News news: newsRestResults.getResultEntity()){
                                newsItemList.add(new NewsItem(news.getTitle(),
                                        news.getUrl(), news.getBody(),
                                        news.getImageurl(), news.getSource(),
                                        news.getPublishedOn()));
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
                break;
            case 2:
                //Observable instance from EasyRest
                NewsService.getPlainObservableNews(this).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
                break;

                default:
                    //Wrapped observable call
                    NewsService.getObservableNews(this, true, new Action1<RestResults<News[]>>() {
                        @Override
                        public void call(RestResults<News[]> newsRestResults) {
                            Parcelable recyclerViewState;
                            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                            if(newsRestResults.isSuccessful()){
                                for(News news: newsRestResults.getResultEntity()){
                                    newsItemList.add(new NewsItem(news.getTitle(),
                                            news.getUrl(), news.getBody(),
                                            news.getImageurl(), news.getSource(),
                                            news.getPublishedOn()));
                                }
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                            }else{
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
        }

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
                                        //getNewsRequest();
                                        getNewsObservable(2);
                                    }
                                }
        );
    }

    @Override
    public void onRefresh() {
        //getNewsRequest();
        getNewsObservable(1);

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
                getNewsObservable(1);
                return true;
        }
        finish();
        return true;
    }
}
