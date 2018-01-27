package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.cryptobuddy.ryanbridges.cryptobuddy.BaseAnimationActivity;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;

/**
 * Created by Ryan on 1/21/2018.
 */

public class CurrencyListTabsActivity extends BaseAnimationActivity implements ViewPager.OnPageChangeListener {

    private SectionsPagerAdapterCurrencyList mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static String baseImageURL = "";
    public static String SYMBOL = "SYMBOL";
    private Toolbar mToolbar;
    boolean doubleBackToExitPressedOnce = false;
    public static String IMAGE_URL_FORMAT = "https://files.coinmarketcap.com/static/img/coins/64x64/%s.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_list_tabs);
//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        mToolbar = (Toolbar) findViewById(R.id.toolbar_currency_list);
//        mToolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.currency_list_tabs);
//        setContentView(tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.currency_list_tabs_container);
        mSectionsPagerAdapter = new SectionsPagerAdapterCurrencyList(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        Fragment fragment = mSectionsPagerAdapter.getFragment(position);
        if (fragment != null) {
            fragment.onResume();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onLeaveThisActivity() {
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tap back again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
