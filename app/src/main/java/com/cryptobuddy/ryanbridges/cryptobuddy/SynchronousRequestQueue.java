package com.cryptobuddy.ryanbridges.cryptobuddy;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;

/**
 * Created by Ryan on 8/12/2017.
 */

public class SynchronousRequestQueue extends RequestQueue {

    public SynchronousRequestQueue(Cache cache, Network network) {
        super(cache, network, 1);
    }
}
