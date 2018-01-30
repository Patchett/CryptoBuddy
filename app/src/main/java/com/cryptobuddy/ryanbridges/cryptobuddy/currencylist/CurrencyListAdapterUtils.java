package com.cryptobuddy.ryanbridges.cryptobuddy.currencylist;

import android.widget.TextView;

/**
 * Created by Ryan on 1/28/2018.
 */

public class CurrencyListAdapterUtils {

    public static void setPercentChangeTextView(TextView textView, String pctChange, String time,
                                                String negativePercentStringResource, String positivePercentStringResource,
                                                int negativeRedColor, int positiveGreenColor, String pctChangeNotAvailableStringResource) {
        if (pctChange == null) {
            textView.setText(String.format(pctChangeNotAvailableStringResource, time));
//            textView.setTextColor(positiveGreenColor);
        } else {
            double change = Double.parseDouble(pctChange);
            if (change < 0) {
                textView.setText(String.format(negativePercentStringResource, time, change));
                textView.setTextColor(negativeRedColor);
            } else {
                textView.setText(String.format(positivePercentStringResource, time, change));
                textView.setTextColor(positiveGreenColor);
            }
        }
    }

}
