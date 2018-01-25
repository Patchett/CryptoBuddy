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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.MonthSlashDayDateFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.MonthSlashYearFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.TimeDateFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.YAxisPriceFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.VolleySingleton;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;

/**
 * A placeholder fragment containing a simple view.
 */
public class GraphFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnChartValueSelectedListener {

    private final static String VOL_URL = "https://poloniex.com/public?command=return24hVolume";
    private final static String CHART_URL_WEEK = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=168&aggregate=1";
    private final static String CHART_URL_ALL_DATA = "https://min-api.cryptocompare.com/data/histoday?fsym=%s&tsym=USD&allData=true";
    private final static String CHART_URL_YEAR = "https://min-api.cryptocompare.com/data/histoday?fsym=%s&tsym=USD&limit=183&aggregate=2";
    private final static String CHART_URL_MONTH = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=240&aggregate=3";
    private final static String CHART_URL_3_MONTH = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=240&aggregate=14";
    private final static String CHART_URL_1_DAY = "https://min-api.cryptocompare.com/data/histominute?fsym=%s&tsym=USD&limit=144&aggregate=10";
    private final static String TICKER_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=%s&tsyms=USD";
    private String formattedTickerURL;
    private int chartFillColor;
    private int chartBorderColor;
    private String crypto;
    private int percentageColor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart lineChart;
    private View rootView;
    private CustomViewPager viewPager;
    private String currentChartURL;
    private IAxisValueFormatter XAxisFormatter;
    public final IAxisValueFormatter monthSlashDayXAxisFormatter = new MonthSlashDayDateFormatter();
    public final TimeDateFormatter dayCommaTimeDateFormatter = new TimeDateFormatter();
    public final MonthSlashYearFormatter monthSlashYearFormatter = new MonthSlashYearFormatter();
    private String currentTimeWindow = "";


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
        dataSet.setHighlightLineWidth(2);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setHighLightColor(chartBorderColor); // color for highlight indicator
        return dataSet;
    }

    public void getChartDataRequest() {
        final TextView percentChangeText = (TextView) rootView.findViewById(R.id.percent_change);
        final TextView currPriceText = (TextView) rootView.findViewById(R.id.current_price);
        final TextView noChartText = (TextView) rootView.findViewById(R.id.noChartDataText);
        noChartText.setEnabled(false);
        lineChart.setEnabled(true);
        noChartText.setText("");
        JsonObjectRequest chartDataRequest = new JsonObjectRequest(Request.Method.GET, currentChartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        List<Entry> closePrices = new ArrayList<Entry>();
                        try {
                            JSONArray rawData = response.getJSONArray("Data");
                            Log.d("I", "rawData: " + rawData);
                            if (rawData.length() == 0) { // Prevents a crash if we get an empty resposne
                                swipeRefreshLayout.setRefreshing(false);
                                noChartText.setEnabled(true);
                                lineChart.setData(null);
                                lineChart.setNoDataText("");
                                noChartText.setText(getResources().getString(R.string.noChartDataString));
                                lineChart.setEnabled(false);
                                lineChart.invalidate();
                                percentChangeText.setText("");
                                currPriceText.setText("");
                                return;
                            }
                            Log.d("I", "rawData: " + rawData);
                            for (int i = 0; i < rawData.length(); i++) {
                                JSONObject row = rawData.getJSONObject(i);
                                double closePrice = row.getDouble("close");
                                double unixSeconds = row.getDouble("time");
                                closePrices.add(new Entry((float) unixSeconds, (float) closePrice));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                        TextView currentPriceTextView = (TextView) rootView.findViewById(R.id.current_price);
                        float currPrice = closePrices.get(closePrices.size() - 1).getY();
                        currentPriceTextView.setText(String.format(getString(R.string.price_format_no_word), currPrice));
                        currentPriceTextView.setTextColor(Color.BLACK);
                        float firstPrice = closePrices.get(0).getY();
                        // Handle edge case where we dont have data for the interval on the chart. E.g. user selects
                        // 3 month window, but we only have data for last month
                        for (Entry e: closePrices) {
                            if (firstPrice != 0) {
                                break;
                            } else {
                                firstPrice = e.getY();
                            }
                        }
                        float difference = (currPrice - firstPrice);
                        float percentChange = (difference / firstPrice) * 100;
                        if (percentChange < 0) {
                            percentChangeText.setText(String.format(getString(R.string.negative_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math.abs(difference)));
                        } else {
                            percentChangeText.setText(String.format(getString(R.string.positive_variable_pct_change_with_dollars_format), currentTimeWindow, percentChange, Math.abs(difference)));
                        }
                        setColors(percentChange);
                        percentChangeText.setTextColor(percentageColor);
                        LineDataSet dataSet = setUpLineDataSet(closePrices);
                        LineData lineData = new LineData(dataSet);
                        lineChart.setDoubleTapToZoomEnabled(false);
                        lineChart.setScaleEnabled(false);
                        lineChart.getDescription().setEnabled(false);
                        lineChart.setData(lineData);
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
                        lineChart.getLegend().setEnabled(false);
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setAvoidFirstLastClipping(true);
                        lineChart.getAxisLeft().setEnabled(true);
                        lineChart.getAxisLeft().setDrawGridLines(false);
                        lineChart.getXAxis().setDrawGridLines(false);
                        lineChart.getAxisLeft().setValueFormatter(new YAxisPriceFormatter());
                        lineChart.getAxisRight().setEnabled(false);
                        xAxis.setDrawAxisLine(true);
                        xAxis.setValueFormatter(XAxisFormatter);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
                        lineChart.invalidate();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("I", "Server Error: " + e.getMessage());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(chartDataRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        lineChart = (LineChart) rootView.findViewById(R.id.chart);
        lineChart.setOnChartValueSelectedListener(this);
        viewPager = (CustomViewPager) container;
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        crypto = getArguments().getString(ARG_SYMBOL);
        currentTimeWindow = String.format(getString(R.string.AllTime));
        formattedTickerURL = String.format(TICKER_URL, crypto);
        currentChartURL = String.format(CHART_URL_ALL_DATA, crypto);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getChartDataRequest();
                                    }
                                }
        );
        XAxisFormatter = monthSlashDayXAxisFormatter;
        Button oneMonthButton = (Button) rootView.findViewById(R.id.monthButton);
        Button threeMonthButton = (Button) rootView.findViewById(R.id.threeMonthButton);
        Button oneWeekButton = (Button) rootView.findViewById(R.id.weekButton);
        Button oneDayButton = (Button) rootView.findViewById(R.id.oneDayButton);
        Button yearButton = (Button) rootView.findViewById(R.id.oneYearButton);
        Button allTimeButton = (Button) rootView.findViewById(R.id.allTimeButton);

        oneDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_1_DAY, crypto);
                currentTimeWindow = String.format(getString(R.string.oneDay));
                XAxisFormatter = dayCommaTimeDateFormatter;
                lineChart.getXAxis().setLabelCount(6);
                onRefresh();
            }
        });
        oneWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_WEEK, crypto);
                currentTimeWindow = String.format(getString(R.string.Week));
                XAxisFormatter = monthSlashDayXAxisFormatter;
                onRefresh();
            }
        });
        oneMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_MONTH, crypto);
                currentTimeWindow = String.format(getString(R.string.Month));
                XAxisFormatter = monthSlashDayXAxisFormatter;
                onRefresh();
            }
        });
        threeMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_3_MONTH, crypto);
                currentTimeWindow = String.format(getString(R.string.threeMonth));
                XAxisFormatter = monthSlashDayXAxisFormatter;
                onRefresh();
            }
        });
        allTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_ALL_DATA, crypto);
                currentTimeWindow = String.format(getString(R.string.AllTime));
                XAxisFormatter = monthSlashYearFormatter;
                onRefresh();
            }
        });
        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentChartURL = String.format(CHART_URL_YEAR, crypto);
                currentTimeWindow = String.format(getString(R.string.Year));
                XAxisFormatter = monthSlashYearFormatter;
                onRefresh();
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        getChartDataRequest();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        TextView currentPrice = (TextView) rootView.findViewById(R.id.current_price);
        TextView dateTextView = (TextView) rootView.findViewById(R.id.graphFragmentDateTextView);
        currentPrice.setText(String.format(getString(R.string.price_format_no_word), e.getY()));
        dateTextView.setText(getFormattedFullDate(e.getX()));
    }

    @Override
    public void onNothingSelected() {

    }

    public String getFormattedFullDate(float unixSeconds) {
        Date date = new Date((int)unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        return sdf.format(date);
    }

}