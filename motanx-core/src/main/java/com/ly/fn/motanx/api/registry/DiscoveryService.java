package com.ly.fn.motanx.api.registry;

import java.util.List;

import com.ly.fn.motanx.api.rpc.URL;

public interface DiscoveryService {

    void subscribe(URL url, NotifyListener listener);

    void unsubscribe(URL url, NotifyListener listener);

    List<URL> discover(URL url);
}
