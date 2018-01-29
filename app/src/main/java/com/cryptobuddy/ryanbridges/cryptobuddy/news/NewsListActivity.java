package com.cryptobuddy.ryanbridges.cryptobuddy.news;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cryptobuddy.ryanbridges.cryptobuddy.BaseAnimationActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomItemClickListener;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.News;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.NewsService;
import com.grizzly.rest.Model.RestResults;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;


/**
 * Created by Ryan on 12/28/2017.
 */

public class NewsListActivity extends BaseAnimationActivity implements SwipeRefreshLayout.OnRefreshListener {

    private NewsListAdapter adapter;
    private List<NewsItem> newsItemList;
    private RecyclerView recyclerView;
    private AppCompatActivity mActivity;
    private Toolbar mToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Observable newsObservable;

    
    public void getNewsObservable(int whatToDo, boolean cache){

        //Example of framework isolation by using observables
        //An standard Rx Action.
        Action1<News[]> subscriber = new Action1<News[]>() {
            @Override
            public void call(News[] newsRestResults) {

                if(newsRestResults != null && newsRestResults.length > 0){

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
                    Log.e("News", "call successful");
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                    Log.e("News", "call failed");
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
                if(newsObservable == null) {
                    newsObservable = NewsService.getPlainObservableNews(this, cache).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                }
                newsObservable.subscribe(subscriber);
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
        setContentView(R.layout.activity_news_list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_news_list);
        setSupportActionBar(mToolbar);
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
    }

    @Override
    public void onRefresh() {
        getNewsObservable(2, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_refresh_button:
                getNewsObservable(2, false);
                return true;
            default:
                finish();
                return true;
        }
    }

    //TODO: An advantage about using observables is how easily they allow to avoid lifecycle crashes.
    //If we were using a listener, there's a chance the callback runs at a moment when accesing some
    // elements (like widgets) is illegal/impossible. While we can void listeners to avoid that,
    // depending on the framework, that may not be possible. Also, Rx observers aren't part of a
    // particular library, so they allow for greater isolation between your app's layers.
    @Override
    public void onPause(){
        super.onPause();
        //Here, we essentially tell RX to ignore any subscriptions done in the UI thread
        if(newsObservable!=null) newsObservable.unsubscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onResume(){
        super.onResume();
        //Here, we make the activity call the news service everytime it cames back from being paused
        if(swipeRefreshLayout!=null){
            swipeRefreshLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            swipeRefreshLayout.setRefreshing(true);
                                            getNewsObservable(2, true);
                                        }
                                    }
            );
        }else {
            onRefresh();
        }
    }
}
