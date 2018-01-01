package com.cryptobuddy.ryanbridges.cryptobuddy.ChartsAndPriceData;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cryptobuddy.ryanbridges.cryptobuddy.CurrencyList.CurrencyListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.News.NewsListActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

/**
 * Created by Ryan on 12/17/2017.
 */

public class CurrencyTabsActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_tabs_activity);
        String symbol = getIntent().getStringExtra(CurrencyListActivity.SYMBOL);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), symbol);
        mViewPager = (ViewPager) findViewById(R.id.container);
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
