package com.cryptobuddy.ryanbridges.cryptobuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 1/6/2018.
 */

public class DatabaseHelperSingleton extends SQLiteOpenHelper {

    private static DatabaseHelperSingleton sInstance;

    private static final String DATABASE_NAME = "Cryptobuddy.db";
    private static final String DATABASE_TABLE = "favorites_list";
    private static final int DATABASE_VERSION = 1;
    private static final String FAVORITE_COINS_COL_0 = "ID";
    private static final String FAVORITE_COINS_COL_1 = "FAVORITES";
    private static final String DEFAULT_FAVORITE_COINS = "BTC,ETH,XRP,LTC,BCH";

    public static synchronized DatabaseHelperSingleton getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelperSingleton(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelperSingleton(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    @Override
    public void onCreate (SQLiteDatabase db) {
        Log.d("DB", "Inside of onCreate database");
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + FAVORITE_COINS_COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FAVORITE_COINS_COL_1 + " TEXT)");
        List<String> defaultCoinsList = new ArrayList<String>(Arrays.asList(DEFAULT_FAVORITE_COINS.split(",")));
        Log.d("DB", "defaultCoinsList: " + defaultCoinsList);
        Gson gson = new Gson();
        String favoritesListString = gson.toJson(defaultCoinsList);
        Log.d("DB", "favoritesListString: " + favoritesListString);
        ContentValues defaultFavoriteCoins = new ContentValues();
        defaultFavoriteCoins.put(FAVORITE_COINS_COL_1, favoritesListString);
        db.insert(DATABASE_TABLE, null, defaultFavoriteCoins);
    }

    public CoinFavoritesStructures getFavorites() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select FAVORITES from " + DATABASE_TABLE, null);
        cursor.moveToPosition(0);
        Log.d("DB", "cursor: " + cursor);
        String favoritesListString = cursor.getString(0);
        Log.d("DB", "favoritesListString in CoinFavoritesStructures: " + favoritesListString);
        cursor.close();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        ArrayList<String> favoritesList = gson.fromJson(favoritesListString, type);
        HashMap<String, String> favoritesMap = new HashMap<String, String>();
        for (int i = 0; i < favoritesList.size(); i++) {
            favoritesMap.put(favoritesList.get(i), favoritesList.get(i));
        }
        return new CoinFavoritesStructures(favoritesList, favoritesMap);
    }

}
