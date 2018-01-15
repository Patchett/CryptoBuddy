package com.cryptobuddy.ryanbridges.cryptobuddy.chartandprice;

/**
 * Created by Ryan on 8/11/2017.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.CustomViewPager;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.VolleySingleton;
import com.cryptobuddy.ryanbridges.cryptobuddy.currencylist.CurrencyListActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;

/**
 * A placeholder fragment containing a simple view.
 */
public class GraphFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String VOL_URL = "https://poloniex.com/public?command=return24hVolume";
    private final static String CHART_URL_WEEK = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=168&aggregate=1";
    private final static String CHART_URL_MONTH = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=240&aggregate=3";
    private final static String TICKER_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=%s&tsyms=USD";
    private String formattedTickerURL;
    private String formattedChartURL;
    private int chartFillColor;
    private int chartBorderColor;
    private String crypto;
    private int percentageColor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart lineChart;
    private View rootView;
    private String TAG = CurrencyListActivity.class.getSimpleName();
    private CustomViewPager viewPager;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SYMBOL = "symbol";

    public GraphFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphFragment newInstance(String symbol) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SYMBOL, symbol);
        fragment.setArguments(args);
        return fragment;
    }

    public void setColors(float percentChange) {
        if (percentChange >= 0) {
            chartFillColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.materialLightGreen, null);
            chartBorderColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.darkGreen, null);
            percentageColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.percentPositiveGreen, null);
        }
        else {
            chartFillColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.materialLightRed, null);
            chartBorderColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.darkRed, null);
            percentageColor = ResourcesCompat.getColor(getActivity().getResources(), R.color.percentNegativeRed, null);
        }
    }

    public LineDataSet setUpLineDataSet(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Price");
        dataSet.setColor(chartBorderColor);
        dataSet.setFillColor(chartFillColor);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(chartBorderColor);
        dataSet.setDrawCircleHole(false);
        dataSet.setDrawValues(false);
        dataSet.setCircleRadius(1);
        return dataSet;
    }

    public void getTickerRequest() {
        final TextView currentPrice = (TextView) rootView.findViewById(R.id.current_price);
        swipeRefreshLayout.setRefreshing(true);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, formattedTickerURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("I", "Ticker response: " + response);
                            JSONObject rawData = response.getJSONObject("RAW").getJSONObject(crypto).getJSONObject("USD");
                            float currPrice = Float.valueOf(rawData.getString("PRICE"));
                            currentPrice.setText(String.format(getString(R.string.price_format_no_word), currPrice));
                            currentPrice.setTextColor(Color.BLACK);
                            JsonObjectRequest chartDataRequest = getChartDataRequest(currPrice);
                            VolleySingleton.getInstance().addToRequestQueue(chartDataRequest) ;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, "Server Error: " + e.getMessage());
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
        VolleySingleton.getInstance().addToRequestQueue(request);
        }

    public JsonObjectRequest getChartDataRequest(final float currPrice) {
        final TextView percentChangeText = (TextView) rootView.findViewById(R.id.percent_change);
        return new JsonObjectRequest(Request.Method.GET, formattedChartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        List<Entry> closePrices = new ArrayList<Entry>();
                        try {
                            JSONArray rawData = response.getJSONArray("Data");
                            Log.d("I", "rawData: " + rawData);
                            for (int i = 0; i < rawData.length(); i++) {
                                JSONObject row = rawData.getJSONObject(i);
                                double closePrice = row.getDouble("close");
                                double unixSeconds = row.getDouble("time");
                                closePrices.add(new Entry((float) unixSeconds, (float) closePrice));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        float firstPrice = closePrices.get(0).getY();
                        float difference = (currPrice - firstPrice);
                        float percentChange = (difference / currPrice) * 100;
                        if (percentChange < 0) {
                            percentChangeText.setText(String.format(getString(R.string.negative_pct_change_with_dollars_format), percentChange, Math.abs(difference)));
                        } else {
                            percentChangeText.setText(String.format(getString(R.string.positive_pct_change_with_dollars_format), percentChange, Math.abs(difference)));
                        }
                        setColors(percentChange);
                        percentChangeText.setTextColor(percentageColor);
                        LineDataSet dataSet = setUpLineDataSet(closePrices);
                        LineData lineData = new LineData(dataSet);
                        lineChart.setDoubleTapToZoomEnabled(false);
                        lineChart.setScaleEnabled(false);
                        lineChart.getDescription().setEnabled(false);
                        lineChart.setData(lineData);
                        lineChart.setNoDataText("Pulling price data...");
                        lineChart.setContentDescription("");
                        lineChart.animateX(1000);
                        lineChart.setOnChartGestureListener(new OnChartGestureListener() {
                            @Override
                            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                                viewPager.setPagingEnabled(false);
                                swipeRefreshLayout.setEnabled(false);
                            }

                            @Override
                            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                                viewPager.setPagingEnabled(true);
                                swipeRefreshLayout.setEnabled(true);
                            }

                            @Override
                            public void onChartLongPressed(MotionEvent me) {

                            }

                            @Override
                            public void onChartDoubleTapped(MotionEvent me) {

                            }

                            @Override
                            public void onChartSingleTapped(MotionEvent me) {

                            }

                            @Override
                            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                            }

                            @Override
                            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                            }

                            @Override
                            public void onChartTranslate(MotionEvent me, float dX, float dY) {

                            }
                        });
//                        lineChart.setBorderWidth(3);
//                        lineChart.setBorderColor(chartBorderColor);
//                        lineChart.setDrawBorders(true);
                        lineChart.getLegend().setEnabled(false);
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setAvoidFirstLastClipping(true);
                        lineChart.getAxisLeft().setEnabled(true);
                        lineChart.getAxisLeft().setDrawGridLines(false);
                        lineChart.getXAxis().setDrawGridLines(false);
                        lineChart.getAxisLeft().setValueFormatter(new YAxisPriceFormatter());
                        lineChart.getAxisRight().setEnabled(false);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setValueFormatter(new XAxisDateFormatter());
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                        lineChart.invalidate();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e(TAG, "Server Error: " + e.getMessage());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        lineChart = (LineChart) rootView.findViewById(R.id.chart);
        viewPager = (CustomViewPager) container;
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        crypto = getArguments().getString(ARG_SYMBOL);
        formattedChartURL = String.format(CHART_URL_WEEK, crypto);
        formattedTickerURL = String.format(TICKER_URL, crypto);
        Log.d(TAG, formattedChartURL);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getTickerRequest();
                                    }
                                }
        );
        return rootView;
    }

    @Override
    public void onRefresh() {
        getTickerRequest();
    }
}