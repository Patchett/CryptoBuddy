package com.cryptobuddy.ryanbridges.cryptobuddy;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Locale;

/**
 * Created by Ryan on 8/10/2017.
 */

public class YAxisPriceFormatter implements IAxisValueFormatter {

    @Override
    public String getFormattedValue(float money, AxisBase axis) {
        String moneyString = String.format(Locale.ENGLISH, "%.2f", money);
        return "$" + moneyString;
    }
}
