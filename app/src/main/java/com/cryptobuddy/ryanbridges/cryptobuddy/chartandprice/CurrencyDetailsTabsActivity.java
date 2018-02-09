package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cryptobuddy.ryanbridges.cryptobuddy.BaseAnimationActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.news.NewsListActivity;

/**
 * Created by Ryan on 12/17/2017.
 */

public class CurrencyDetailsTabsActivity extends BaseAnimationActivity {
    private SectionsPagerAdapterGraphs mSectionsPagerAdapter;
    public CustomViewPager mViewPager;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_details_tabs);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_currency_details);
        setSupportActionBar(mToolbar);
        String symbol = getIntent().getStringExtra(GraphFragment.ARG_SYMBOL);
        String id = getIntent().getStringExtra(GraphFragment.ARG_ID);
        mViewPager = (CustomViewPager) findViewById(R.id.currencyTabsViewPager);
        mSectionsPagerAdapter = new SectionsPagerAdapterGraphs(getSupportFragmentManager(), symbol, id);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        getSupportActionBar().setTitle(symbol);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.currency_tabs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.news_button:
                startActivity(new Intent(this, NewsListActivity.class));
                return true;
        }
        finish();
        return true;
    }

}
