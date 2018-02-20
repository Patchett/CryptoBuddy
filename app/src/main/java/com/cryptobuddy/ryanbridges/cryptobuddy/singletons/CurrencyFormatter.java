package com.cryptobuddy.ryanbridges.cryptobuddy.singletons;

import android.content.Context;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;

import java.text.NumberFormat;

/**
 * Created by Ryan on 2/19/2018.
 */

public class CurrencyFormatter {

    private String symbol;
    private Context context;

    public CurrencyFormatter(String symbol, Context context) {
        this.symbol = symbol;
        this.context = context;
    }

    public String format(float amount) {
        switch (symbol) {
            case "AIC":
            case "USDT":
            case "NZD":
            case "CAD":
            case "CLP":
            case "COP":
            case "HKD":
            case "MXN":
            case "TTD":
            case "XCD":
            case "ARS":
            case "USD":
                return String.format(context.getString(R.string.usd_format), amount);
            default:
                NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
                nf.setMaximumFractionDigits(10);
                return symbol + " " + nf.format(amount);
        }

    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
