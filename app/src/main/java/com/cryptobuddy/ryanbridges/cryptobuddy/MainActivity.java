package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String NEWS_API_KEY = BuildConfig.API_KEY;
    public final static String BTC_NEWS_URL_TEMPLATE = "http://eventregistry.org/json/article?query=%7B\"%24query\"%3A%7B\"%24and\"%3A%5B%7B\"conceptUri\"%3A%7B\"%24and\"%3A%5B\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FEthereum\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FCryptocurrency\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FBitcoin\"%2C\"http%3A%2F%2Fen.wikipedia.org%2Fwiki%2FLitecoin\"%5D%7D%7D%2C%7B\"lang\"%3A\"eng\"%7D%5D%7D%7D&action=getArticles&resultType=articles&articlesSortBy=date&articlesCount=20&apiKey=";
    public final static String BTC_NEWS_URL = BTC_NEWS_URL_TEMPLATE + NEWS_API_KEY;
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
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
    }
}
