package com.cryptobuddy.ryanbridges.cryptobuddy.rest;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.News;
import com.cryptobuddy.ryanbridges.cryptobuddy.singletons.VolleySingleton;
import com.grizzly.rest.GenericRestCall;
import com.grizzly.rest.Model.RestResults;
import com.grizzly.rest.Model.afterTaskCompletion;
import com.grizzly.rest.Model.afterTaskFailure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;

import java.util.Calendar;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by fco on 11-01-18.
 */

public class NewsService {

    public final static String BTC_NEWS_URL = "https://min-api.cryptocompare.com/data/news/";

    public static void getNews(Context context, afterTaskCompletion<News> taskCompletion, afterTaskFailure failure, boolean async) {
        new GenericRestCall<>(Void.class, News.class, String.class)
                .setUrl(BTC_NEWS_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(180000L)
                .setMethodToCall(HttpMethod.GET)
                .setTaskCompletion(taskCompletion)
                .setTaskFailure(failure)
                .setAutomaticCacheRefresh(true).execute(async);
    }

    /**
     * Wrapped rest call using Rx action as a listener.
     * @param context The context for caching
     * @param async Whether the call is asynchronous or not
     * @param myAction a single use subscriber or Action
     */
    public static void getObservableNews(Context context, boolean async, Action1<RestResults<News[]>> myAction) {
               rx.Observable<RestResults<News[]>> myObservable = getObservableNews(context);
               if(async){
                   myObservable.subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe(myAction);
               }else {
                   myObservable.subscribe(myAction);
               }

    }

    /**
     * Creates a GenericRestCall and returns it as an Rx observable. An observable will be fired
     * each time a susbcriber subscribes to it, and multiple subscribers can be attached to a single
     * observable.
     * @param context the context for caching
     * @return an observable returning a RestResults<News[]> object
     */
    public static rx.Observable<RestResults<News[]>> getObservableNews(Context context) {
        return new GenericRestCall<>(Void.class, News[].class, String.class)
                .setUrl(BTC_NEWS_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(30000L)
                .setMethodToCall(HttpMethod.GET)
                .setAutomaticCacheRefresh(true)
                .setReprocessWhenRefreshing(true)
                .asObservable();
    }

    /**
     * Creates a GenericRestCall and returns it as an Rx observable. An observable will be fired
     * each time a susbcriber subscribes to it, and multiple subscribers can be attached to a single
     * observable.
     * @param context the context for caching
     * @return an observable returning a News[] object
     */
    public static rx.Observable<News[]> getPlainObservableNews(Context context) {
        return new GenericRestCall<>(Void.class, News[].class, String.class)
                .setUrl(BTC_NEWS_URL)
                .setContext(context.getApplicationContext())
                .isCacheEnabled(true)
                .setCacheTime(30000L)
                .setMethodToCall(HttpMethod.GET)
                .setAutomaticCacheRefresh(true)
                .setReprocessWhenRefreshing(true)
                .asSimpleObservable();
    }

    /**
     * Calls the volley singleton, without exposing volley and reusing the call structure.
     * @param newsRequest a custom listener for volley calls.
     */
    public static void getNewsVolley(final VolleySingleton.OnVolleyRequest newsRequest){
        Log.e("VolleyCall", "start:"+ Calendar.getInstance().getTime());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, BTC_NEWS_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        newsRequest.parse(response);
                        Log.e("VolleyCall", "done:"+ Calendar.getInstance().getTime());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                newsRequest.onParseError();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(request);
    }
}
