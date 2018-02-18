package com.cryptobuddy.ryanbridges.cryptobuddy.rest;

import android.content.Context;

import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinList;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.TradingPair;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import org.springframework.http.HttpMethod;


/**
 * Created by fco on 11-01-18.
 */

public class CryptoCompareCoinService {

    public static final String ALL_COINS_LIST_URL = "https://min-api.cryptocompare.com/data/all/coinlist";
    public static final String TOP_PAIRS_URL = "https://min-api.cryptocompare.com/data/top/pairs?fsym=%s&limit=20";

    public static void getAllCoins(Context context, afterTaskCompletion<CoinList> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(Void.class, CoinList.class, String.class)
                .setUrl(ALL_COINS_LIST_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(604800000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

    public static void getTopPairs(Context context, String symbol, afterTaskCompletion<TradingPair> taskCompletion, afterTaskFailure failure) {
        String url = String.format(TOP_PAIRS_URL, symbol);
        new GenericRestCall<>(Void.class, TradingPair.class, String.class)
                .setUrl(url)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                // Cache top pairs for 1hr
                .setCacheTime(3600000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(false).execute(true);
    }
}
