package com.cryptobuddy.ryanbridges.cryptobuddy;

/**
 * Created by Ryan on 8/11/2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class GraphFragment extends Fragment {

    public final static String TICKER_URL = "https://poloniex.com/public?command=returnTicker";
    public final static String VOL_URL = "https://poloniex.com/public?command=return24hVolume";
    public final static String CHART_URL = "https://poloniex.com/public?command=returnChartData&currencyPair=USDT_%s&start=%s&end=9999999999&period=14400";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NAME = "section_name";

    public GraphFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphFragment newInstance(String tabName) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, tabName);
        fragment.setArguments(args);
        return fragment;
    }

    public LineDataSet setUpLineDataSet(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(Color.BLACK);
        dataSet.setFillColor(ResourcesCompat.getColor(getResources(), R.color.materialLightGreen, null));
        dataSet.setHighLightColor(ResourcesCompat.getColor(getResources(), R.color.materialLightPurple, null));
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    public JsonObjectRequest getTickerRequest(View rootView) {
        final TextView currentPrice = (TextView) rootView.findViewById(R.id.current_price);
        final TextView percentChangeText = (TextView) rootView.findViewById(R.id.percent_change);
        return new JsonObjectRequest(Request.Method.GET, TICKER_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String currencyPair = String.format(Locale.ENGLISH, "USDT_%s", getArguments().getString(ARG_SECTION_NAME));
                            response = response.getJSONObject(currencyPair);
                            Log.d("I", "TICKER_RESPONSE" + response);
                            float lastValue = Float.valueOf(response.getString("last"));
                            float percentChange = Float.valueOf(response.getString("percentChange"));
                            float amountChange = lastValue * percentChange;
                            currentPrice.setText(String.format(getString(R.string.price_format), lastValue));
                            currentPrice.setTextColor(Color.BLACK);
                            Log.d("I", "amountChange " + amountChange);
                            percentChangeText.setText(String.format(getString(R.string.percent_change_format), Float.valueOf(response.getString("percentChange")) * 100, amountChange));
                            percentChangeText.setTextColor(ResourcesCompat.getColor(getResources(), R.color.percentPositiveGreen, null));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                });
        }

    public JsonArrayRequest getChartDataRequest(View rootView, String formattedChartURL) {
        final LineChart lineChart = (LineChart) rootView.findViewById(R.id.chart);
        return new JsonArrayRequest(Request.Method.GET, formattedChartURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Entry> closePrices = new ArrayList<Entry>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject row = response.getJSONObject(i);
                                int closePrice = row.getInt("close");
                                int unixSeconds = row.getInt("date");
                                closePrices.add(new Entry( (float) unixSeconds, (float) closePrice));
                            }
                            catch (Exception e) {
                                continue;
                            }
                        }
                        LineDataSet dataSet = setUpLineDataSet(closePrices);
                        LineData lineData = new LineData(dataSet);
                        lineChart.setDoubleTapToZoomEnabled(false);
                        lineChart.setScaleEnabled(false);
                        lineChart.getDescription().setEnabled(false);
                        lineChart.setData(lineData);
                        lineChart.setNoDataText("Pulling price data...");
                        lineChart.setContentDescription("");
                        lineChart.setBorderWidth(3);
                        lineChart.setBorderColor(ResourcesCompat.getColor(getResources(), R.color.darkGreen, null));
                        lineChart.setDrawBorders(true);
                        lineChart.getLegend().setEnabled(false);
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setAvoidFirstLastClipping(true);
                        lineChart.getAxisRight().setEnabled(false);
                        lineChart.getAxisLeft().setValueFormatter(new YAxisPriceFormatter());
                        xAxis.setDrawAxisLine(false);
                        xAxis.setValueFormatter(new XAxisDateFormatter());
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        lineChart.invalidate();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.graph_fragment, container, false);
        final LineChart lineChart = (LineChart) rootView.findViewById(R.id.chart);
        lineChart.animateX(1000);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String crypto = getArguments().getString(ARG_SECTION_NAME);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5);
        long fiveDaysAgo = cal.getTimeInMillis() / 1000;
        String formattedChartURL = String.format(CHART_URL, crypto, fiveDaysAgo);
        Log.d("I", formattedChartURL);
        JsonArrayRequest chartDataRequest = getChartDataRequest(rootView, formattedChartURL);
        JsonObjectRequest tickerRequest = getTickerRequest(rootView);
        requestQueue.add(chartDataRequest);
        requestQueue.add(tickerRequest);
        return rootView;
    }
}