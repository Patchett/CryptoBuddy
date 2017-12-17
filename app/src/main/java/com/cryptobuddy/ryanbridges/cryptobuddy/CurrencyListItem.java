package com.cryptobuddy.ryanbridges.cryptobuddy;

/**
 * Created by Ryan on 12/9/2017.
 */

public class CurrencyListItem {
    public String symbol;
    public double currPrice;
    public double change24hr;
    public double changePCT24hr;

    public CurrencyListItem(String symbol, double currPrice, double change24hr, double changePCT24hr) {
        this.symbol = symbol;
        this.currPrice = currPrice;
        this.change24hr = change24hr;
        this.changePCT24hr = changePCT24hr;
    }
}
