package com.ly.fn.motanx.api.registry;

import java.util.List;

import com.ly.fn.motanx.api.rpc.URL;


/**
 * Notify when service changed.
 */
public interface NotifyListener {

    void notify(URL registryUrl, List<URL> urls);
}
