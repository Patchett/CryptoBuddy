package com.cryptobuddy.ryanbridges.cryptobuddy.rest;

import android.content.Context;

import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import org.springframework.http.HttpMethod;

/**
 * Created by Ryan on 1/16/2018.
 */

public class CoinMarketCapService {

    public static final String COIN_MARKET_CAP_ALL_COINS_URL = "https://api.coinmarketcap.com/v1/ticker/?limit=0";

    public static void getAllCoins(Context context, afterTaskCompletion<CMCCoin[]> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(String.class, CMCCoin[].class, String.class)
                .setUrl(COIN_MARKET_CAP_ALL_COINS_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(300000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

}
