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

import com.cryptobuddy.ryanbridges.cryptobuddy.R;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.MonthSlashDayDateFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.MonthSlashYearFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.TimeDateFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.formatters.YAxisPriceFormatter;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCChartData;
import com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService;
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
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.cryptobuddy.ryanbridges.cryptobuddy.R.color.colorAccent;
import static com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService.COIN_MARKETCAP_CHART_URL_ALL_DATA;
import static com.cryptobuddy.ryanbridges.cryptobuddy.rest.CoinMarketCapService.COIN_MARKETCAP_CHART_URL_WINDOW;

/**
 * A placeholder fragment containing a simple view.
 */
public class GraphFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnChartValueSelectedListener {

    private final static String CHART_URL_WEEK = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=168&aggregate=1";
    private final static String CHART_URL_ALL_DATA = "https://min-api.cryptocompare.com/data/histoday?fsym=%s&tsym=USD&allData=true";
    private final static String CHART_URL_YEAR = "https://min-api.cryptocompare.com/data/histoday?fsym=%s&tsym=USD&limit=183&aggregate=2";
    private final static String CHART_URL_MONTH = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=240&aggregate=3";
    private final static String CHART_URL_3_MONTH = "https://min-api.cryptocompare.com/data/histohour?fsym=%s&tsym=USD&limit=240&aggregate=14";
    private final static String CHART_URL_1_DAY = "https://min-api.cryptocompare.com/data/histominute?fsym=%s&tsym=USD&limit=144&aggregate=10";
    private final static String TICKER_URL = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=%s&tsyms=USD";
    private int chartFillColor;
    private int chartBorderColor;
    private String cryptoSymbol;
    private String cryptoID;
    private int percentageColor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LineChart lineChart;
    private View rootView;
    private CustomViewPager viewPager;
    private IAxisValueFormatter XAxisFormatter;
    public final IAxisValueFormatter monthSlashDayXAxisFormatter = new MonthSlashDayDateFormatter();
    public final TimeDateFormatter dayCommaTimeDateFormatter = new TimeDateFormatter();
    public final MonthSlashYearFormatter monthSlashYearFormatter = new MonthSlashYearFormatter();
    private String currentTimeWindow = "";
    private SingleSelectToggleGroup buttonGroup;
    public static String CURRENT_CHART_URL;


    public static final String ARG_SYMBOL = "symbol";
    public static final String ARG_ID = "ID";

    public GraphFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GraphFragment newInstance(String symbol, String id) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SYMBOL, symbol);
        args.putString(ARG_ID, id);
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

    public void getCMCChart() {
        final TextView percentChangeText = (TextView) rootView.findViewById(R.id.percent_change);
        final TextView noChartText = (TextView) rootView.findViewById(R.id.noChartDataText);
        final TextView currPriceText = (TextView) rootView.findViewById(R.id.current_price);
        noChartText.setEnabled(false);
        lineChart.setEnabled(true);
        noChartText.setText("");
        CoinMarketCapService.getCMCChartData(getActivity(), cryptoID, new afterTaskCompletion<CMCChartData>() {
            @Override
            public void onTaskCompleted(CMCChartData cmcChartData) {
                // TODO: Allow switching chart from BTC to USD
                List<Entry> closePrices = new ArrayList<>();
                for (List<Float> priceTimeUnit : cmcChartData.getPriceUSD()) {
                    closePrices.add(new Entry(priceTimeUnit.get(0), priceTimeUnit.get(1)));
                }
                if (closePrices.size() == 0) {
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
                lineChart.animateX(800);
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
        }, new afterTaskFailure() {
            @Override
            public void onTaskFailed(Object o, Exception e) {
                Log.e("ERROR", "Server Error: " + e.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, true);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setDayChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.oneDay));
        XAxisFormatter = dayCommaTimeDateFormatter;
    }

    public void setWeekChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -7);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Week));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Month));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setThreeMonthChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -3);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.threeMonth));
        XAxisFormatter = monthSlashDayXAxisFormatter;
    }

    public void setYearChecked(Calendar cal) {
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.YEAR, -1);
        long startTime = cal.getTimeInMillis();
        cal.clear();
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_WINDOW, cryptoID, startTime, endTime);
        currentTimeWindow = String.format(getString(R.string.Year));
        XAxisFormatter = monthSlashYearFormatter;
    }

    public void setAllTimeChecked() {
        currentTimeWindow = String.format(getString(R.string.AllTime));
        CURRENT_CHART_URL = String.format(COIN_MARKETCAP_CHART_URL_ALL_DATA, cryptoID);
        XAxisFormatter = monthSlashYearFormatter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_graph, container, false);
        lineChart = (LineChart) rootView.findViewById(R.id.chart);
        lineChart.setOnChartValueSelectedListener(this);
        viewPager = (CustomViewPager) container;
        buttonGroup = (SingleSelectToggleGroup) rootView.findViewById(R.id.chart_interval_button_grp);
        cryptoSymbol = getArguments().getString(ARG_SYMBOL);
        cryptoID = getArguments().getString(ARG_ID);
        setDayChecked(Calendar.getInstance());
        buttonGroup.check(R.id.dayButton);
        currentTimeWindow = String.format(getString(R.string.oneDay));
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(colorAccent);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        getCMCChart();
                                    }
                                }
        );
        buttonGroup.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                Calendar.getInstance();
                switch (checkedId) {
                    case R.id.dayButton:
                        setDayChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.weekButton:
                        setWeekChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.monthButton:
                        setMonthChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.threeMonthButton:
                        setThreeMonthChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.yearButton:
                        setYearChecked(Calendar.getInstance());
                        onRefresh();
                        break;
                    case R.id.allTimeButton:
                        setAllTimeChecked();
                        onRefresh();
                        break;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {
        getCMCChart();
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
        Date date = new Date((long)unixSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        return sdf.format(date);
    }
}