package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;

/**
 * Created by Ryan on 12/29/2017.
 */

public class MarketsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;

    public MarketsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MarketsFragment newInstance(String symbol) {
        MarketsFragment fragment = new MarketsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_SECTION_NAME, symbol);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_markets, container, false);
        return rootView;
    }

    @Override
    public void onRefresh() {
    }

}
