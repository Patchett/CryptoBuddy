package com.cryptobuddy.ryanbridges.cryptobuddy;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ryan on 8/10/2017.
 */

public class XAxisDateFormatter  implements IAxisValueFormatter {


    @Override
    public String getFormattedValue(float unixSeconds, AxisBase axis) {
        Date date = new Date((int)unixSeconds*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd", Locale.ENGLISH);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }
}
