package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterCurrencyList extends FragmentPagerAdapter {

    private String symbol;

    public SectionsPagerAdapterCurrencyList(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a GraphFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return AllCurrencyListFragment.newInstance();
            case 1:
                return FavoriteCurrencyListFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        // Total pages to show
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "All Coins";
            case 1:
                return "Favorites";
        }
        return null;
    }
}
