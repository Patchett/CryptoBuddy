package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapterGraphs extends FragmentPagerAdapter {

    private String symbol;
    private String id;

    public SectionsPagerAdapterGraphs(FragmentManager fm) {
        super(fm);
    }
    public SectionsPagerAdapterGraphs(FragmentManager fm, String symbol, String id) {
        super(fm);
        this.symbol = symbol;
        this.id = id;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a GraphFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return GraphFragment.newInstance(this.symbol, this.id);
            case 1:
                return MarketsFragment.newInstance((String) this.getPageTitle(position));
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
                return "Chart";
            case 1:
                return "Markets";
            default:
                return null;
        }
    }
}
