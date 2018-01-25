package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.cryptobuddy.ryanbridges.cryptobuddy.BaseAnimationActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

/**
 * Created by Ryan on 1/21/2018.
 */

public class CurrencyListTabsActivity extends BaseAnimationActivity {
    private SectionsPagerAdapterCurrencyList mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static String baseImageURL = "";
    public static String SYMBOL = "SYMBOL";
    public static String IMAGE_URL_FORMAT = "https://files.coinmarketcap.com/static/img/coins/64x64/%s.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_tabs);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.currency_list_tabs);
//        setContentView(tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.currency_list_tabs_container);
        mSectionsPagerAdapter = new SectionsPagerAdapterCurrencyList(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    @Override
    protected void onLeaveThisActivity() {
    }
}
