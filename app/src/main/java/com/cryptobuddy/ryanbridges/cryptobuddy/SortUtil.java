package com.cryptobuddy.ryanbridges.cryptobuddy;

import com.cryptobuddy.ryanbridges.cryptobuddy.models.rest.CMCCoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Ryan on 3/6/2018.
 */

public class SortUtil {
    public static final void sortList(ArrayList<CMCCoin> currencyList, int number) {
        switch (number) {
            // Name A-Z
            case 0:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;
            // Market Cap
            case 1:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                    return Integer.parseInt(lhs.getRank()) < Integer.parseInt(rhs.getRank()) ? -1 : Integer.parseInt(lhs.getRank()) > Integer.parseInt(rhs.getRank()) ? +1 : 0;
                    }
                });
                break;
            // Price
            case 2:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        if (lhs.getPrice_usd() == null && rhs.getPrice_usd() == null) {
                            return 0;
                        }
                        if (lhs.getPrice_usd() == null) {
                            return -1;
                        }
                        if (rhs.getPrice_usd() == null) {
                            return 1;
                        }
                        float comp = Float.parseFloat(rhs.getPrice_usd()) - Float.parseFloat(lhs.getPrice_usd());
                        if (comp == 0) {
                            return 0;
                        } else if (comp < 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                break;
            // Volume 24h
            case 3:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        if (lhs.getVolume_usd_24h() == null && rhs.getVolume_usd_24h() == null) {
                            return 0;
                        }
                        if (lhs.getVolume_usd_24h() == null) {
                            return 1;
                        }
                        if (rhs.getVolume_usd_24h() == null) {
                            return -1;
                        }
                        float comp = Float.parseFloat(rhs.getVolume_usd_24h()) - Float.parseFloat(lhs.getVolume_usd_24h());
                        if (comp == 0) {
                            return 0;
                        } else if (comp < 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                break;
            // Change 1h
            case 4:
            // Change 24h
            case 5:
            // Change 7d
            case 6:
            // Name Z-A
            case 7:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;
            // Market Cap LH
            case 8:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        return Integer.parseInt(rhs.getRank()) < Integer.parseInt(lhs.getRank()) ? -1 : Integer.parseInt(rhs.getRank()) > Integer.parseInt(lhs.getRank()) ? +1 : 0;
                    }
                });
                break;
            // Price LH
            case 9:
                Collections.sort(currencyList, new Comparator<CMCCoin>() {
                    @Override
                    public int compare(CMCCoin lhs, CMCCoin rhs) {
                        if (lhs.getPrice_usd() == null && rhs.getPrice_usd() == null) {
                            return 0;
                        }
                        if (lhs.getPrice_usd() == null || rhs.getPrice_usd() == null) {
                            return Integer.parseInt(rhs.getRank()) < Integer.parseInt(lhs.getRank()) ? -1 : Integer.parseInt(rhs.getRank()) > Integer.parseInt(lhs.getRank()) ? +1 : 0;
                        }
                        float comp = Float.parseFloat(rhs.getPrice_usd()) - Float.parseFloat(lhs.getPrice_usd());
                        if (comp == 0) {
                            return 0;
                        } else if (comp < 0) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                break;
            // Volume 24h LH
            case 10:
            // Change 1h LH
            case 11:
            // Change 24h LH
            case 12:
            // Change 7d LH
            case 13:
        }

    }
}
