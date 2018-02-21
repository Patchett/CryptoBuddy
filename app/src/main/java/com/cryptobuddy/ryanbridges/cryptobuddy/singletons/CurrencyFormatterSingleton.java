package com.cryptobuddy.ryanbridges.cryptobuddy.singletons;

import android.content.Context;

import com.cryptobuddy.ryanbridges.cryptobuddy.R;

import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by Ryan on 2/19/2018.
 */

public class CurrencyFormatterSingleton {

    private Context context;
    private HashMap<String, String> currencyFormatMap;
    private static CurrencyFormatterSingleton sInstance;


    private CurrencyFormatterSingleton(Context context) {
        this.context = context;
        currencyFormatMap = new HashMap<String, String>();
        buildHashMap();
    }

    public static synchronized CurrencyFormatterSingleton getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new CurrencyFormatterSingleton(context.getApplicationContext());
        }
        return sInstance;
    }

    public void buildHashMap() {
        currencyFormatMap.put("AIC", context.getString(R.string.usd_format));
        currencyFormatMap.put("USDT", context.getString(R.string.usd_format));
        currencyFormatMap.put("NZD", context.getString(R.string.usd_format));
        currencyFormatMap.put("CAD", context.getString(R.string.usd_format));
        currencyFormatMap.put("CLP", context.getString(R.string.usd_format));
        currencyFormatMap.put("COP", context.getString(R.string.usd_format));
        currencyFormatMap.put("HKD", context.getString(R.string.usd_format));
        currencyFormatMap.put("MXN", context.getString(R.string.usd_format));
        currencyFormatMap.put("TTD", context.getString(R.string.usd_format));
        currencyFormatMap.put("XCD", context.getString(R.string.usd_format));
        currencyFormatMap.put("ARS", context.getString(R.string.usd_format));
        currencyFormatMap.put("USD", context.getString(R.string.usd_format));
        currencyFormatMap.put("AUD", context.getString(R.string.aud_format));
        currencyFormatMap.put("BRL", context.getString(R.string.brl_format));
        currencyFormatMap.put("BSD", context.getString(R.string.bsd_format));
        currencyFormatMap.put("CHF", context.getString(R.string.chf_format));
        currencyFormatMap.put("CNY", context.getString(R.string.cny_format));
        currencyFormatMap.put("CZK", context.getString(R.string.czk_format));
        currencyFormatMap.put("DKK", context.getString(R.string.dkk_format));
        currencyFormatMap.put("EUR", context.getString(R.string.euro_format));
        currencyFormatMap.put("FJD", context.getString(R.string.fjd_format));
        currencyFormatMap.put("GBP", context.getString(R.string.gbp_format));
        currencyFormatMap.put("GHS", context.getString(R.string.ghs_format));
        currencyFormatMap.put("GTQ", context.getString(R.string.gtq_format));
        currencyFormatMap.put("HNL", context.getString(R.string.hnl_format));
        currencyFormatMap.put("HRK", context.getString(R.string.hrk_format));
        currencyFormatMap.put("HUF", context.getString(R.string.huf_format));
        currencyFormatMap.put("IDR", context.getString(R.string.idr_format));
        currencyFormatMap.put("ILS", context.getString(R.string.ils_format));
        currencyFormatMap.put("INR", context.getString(R.string.inr_format));
        currencyFormatMap.put("ISK", context.getString(R.string.isk_format));
        currencyFormatMap.put("NOK", context.getString(R.string.isk_format));
        currencyFormatMap.put("SEK", context.getString(R.string.isk_format));
        currencyFormatMap.put("JMD", context.getString(R.string.jmd_format));
        currencyFormatMap.put("JPY", context.getString(R.string.jpy_format));
        currencyFormatMap.put("LKR", context.getString(R.string.lkr_format));
        currencyFormatMap.put("PKR", context.getString(R.string.lkr_format));
        currencyFormatMap.put("MMK", context.getString(R.string.mmk_format));
        currencyFormatMap.put("MYR", context.getString(R.string.myr_format));
        currencyFormatMap.put("PAB", context.getString(R.string.pab_format));
        currencyFormatMap.put("PEN", context.getString(R.string.pen_format));
        currencyFormatMap.put("PHP", context.getString(R.string.php_format));
        currencyFormatMap.put("PLN", context.getString(R.string.pln_format));
        currencyFormatMap.put("RON", context.getString(R.string.ron_format));
        currencyFormatMap.put("RSD", context.getString(R.string.rsd_format));
        currencyFormatMap.put("RUB", context.getString(R.string.rub_format));
        currencyFormatMap.put("SGD", context.getString(R.string.sgd_format));
        currencyFormatMap.put("THB", context.getString(R.string.thb_format));
        currencyFormatMap.put("TRY", context.getString(R.string.try_format));
        currencyFormatMap.put("TWD", context.getString(R.string.twd_format));
        currencyFormatMap.put("VEF", context.getString(R.string.vef_format));
        currencyFormatMap.put("XAF", context.getString(R.string.xaf_format));
        currencyFormatMap.put("XPF", context.getString(R.string.xpf_format));
        currencyFormatMap.put("ZAR", context.getString(R.string.zar_format));
    }

    public String format(float amount, String currency) {
        if (currencyFormatMap.get(currency) != null) {
            return String.format(currencyFormatMap.get(currency), amount);
        } else {
            NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
            nf.setMaximumFractionDigits(10);
            return currency + " " + nf.format(amount);
        }

    }
}
