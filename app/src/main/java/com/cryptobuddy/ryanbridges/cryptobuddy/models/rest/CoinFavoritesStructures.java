package com.cryptobuddy.ryanbridges.cryptobuddy.models.rest;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Ryan on 1/6/2018.
 */

public class CoinFavoritesStructures {
    public List<String> favoriteList;
    public HashMap<String, String> favoritesMap;

    public CoinFavoritesStructures(List<String> favoriteList, HashMap<String, String> favoritesMap) {
        this.favoriteList = favoriteList;
        this.favoritesMap = favoritesMap;
    }
}
