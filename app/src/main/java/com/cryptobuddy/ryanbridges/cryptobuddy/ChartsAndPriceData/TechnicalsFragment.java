package com.cryptobuddy.ryanbridges.cryptobuddy.ChartsAndPriceData;

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

public class TechnicalsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View rootView;

    public TechnicalsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TechnicalsFragment newInstance(String symbol) {
        TechnicalsFragment fragment = new TechnicalsFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_SECTION_NAME, symbol);
//        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.technical_fragment, container, false);
        return rootView;
    }

    @Override
    public void onRefresh() {
    }

}
