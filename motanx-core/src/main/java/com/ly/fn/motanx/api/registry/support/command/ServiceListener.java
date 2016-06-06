package com.ly.fn.motanx.api.registry.support.command;

import java.util.List;

import com.ly.fn.motanx.api.rpc.URL;

public interface ServiceListener {

    void notifyService(URL refUrl, URL registryUrl, List<URL> urls);

}
