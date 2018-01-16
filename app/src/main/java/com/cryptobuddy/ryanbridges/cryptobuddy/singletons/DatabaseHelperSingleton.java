package com.cryptobuddy.ryanbridges.cryptobuddy.singletons;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CoinFavoritesStructures;
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

    // Use singleton design pattern so there is only ever one DB object floating around
    public static synchronized DatabaseHelperSingleton getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelperSingleton(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelperSingleton(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Run this method when the DB Schema is upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    // Run this method the first time the DB is ever created
    @Override
    public void onCreate (SQLiteDatabase db) {
        // Drop the table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        // Create the new table with 2 columns. One for ID and one for the coin list
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + FAVORITE_COINS_COL_0 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FAVORITE_COINS_COL_1 + " TEXT)");
        // Convert the defaultCoinsList string into a list of strings. Use comma as the delimeter
        List<String> defaultCoinsList = new ArrayList<String>(Arrays.asList(DEFAULT_FAVORITE_COINS.split(",")));
        // Instantiate a serializer so we can easily load/store the list in the DB
        Gson gson = new Gson();
        // Serialize the list of strings into a JSON payload that we can store in the DB
        String favoritesListString = gson.toJson(defaultCoinsList);
        // Put the serialized paylout into a ContentValues object to prepare it for storage
        ContentValues defaultFavoriteCoins = new ContentValues();
        defaultFavoriteCoins.put(FAVORITE_COINS_COL_1, favoritesListString);
        // Insert the list into the DB
        db.insert(DATABASE_TABLE, null, defaultFavoriteCoins);
    }

    // Grab the user's list of favorite coins out of the DB and save them into an object that
    // represents the coins as a list and as a hashtable. The hashtable can be used for fast lookups
    // The list will maintain the order of the user's favorite coins
    public CoinFavoritesStructures getFavorites() {
        // Get a connection to the DB
        SQLiteDatabase db = this.getReadableDatabase();
        // Pull the favorites out of the DB
        Cursor cursor = db.rawQuery("select FAVORITES from " + DATABASE_TABLE, null);
        // Move the cursor to the first element returned by the query
        cursor.moveToPosition(0);
        // Get the list represented as a string out of the cursor
        String favoritesListString = cursor.getString(0);
        cursor.close();
        // Instantiate a new serializer so that we can convert the string from the DB into a real list object
        Gson gson = new Gson();
        // Tell Gson what type we want to convert the serialized string to
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        // Load the string of the user's favorite coins into a list of strings!
        ArrayList<String> favoritesList = gson.fromJson(favoritesListString, type);
        // Instantiate a new hashmap and put all of the items in the list above into the hashmap
        HashMap<String, String> favoritesMap = new HashMap<String, String>();
        for (int i = 0; i < favoritesList.size(); i++) {
            favoritesMap.put(favoritesList.get(i), favoritesList.get(i));
        }
        // Return a wrapper object that holds both the list and the hashmap
        return new CoinFavoritesStructures(favoritesList, favoritesMap);
    }

    // This will allow us to save the user's coin favorites any time by passing in a wrapper object
    // that contains the list of the user's favorite coins as well as the hashtable representation
    public void saveCoinFavorites(CoinFavoritesStructures favs) {
        // Get a writeable connection to the db
        SQLiteDatabase db = this.getWritableDatabase();
        // Instantiate a new serializer so that we can convert our list of coins back into a string
        // that is storable in the DB
        Gson gson = new Gson();
        // Convert the list of the user's favorite coins into a string
        String favoritesListString = gson.toJson(favs.favoriteList);
        // Put the serialized paylout into a ContentValues object to prepare it for storage
        ContentValues newCoinFavorites = new ContentValues();
        newCoinFavorites.put(FAVORITE_COINS_COL_1, favoritesListString);
        // Update the row in the DB with the new list of favorites!
        db.update(DATABASE_TABLE, newCoinFavorites, null, null);
    }
}
