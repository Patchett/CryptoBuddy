package com.cryptobuddy.ryanbridges.cryptobuddy.formatters;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ryan on 1/14/2018.
 */

public class MonthSlashYearFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float unixSeconds, AxisBase axis) {
        Date date = new Date((int)unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM/yy", Locale.ENGLISH);
        return sdf.format(date);
    }
}