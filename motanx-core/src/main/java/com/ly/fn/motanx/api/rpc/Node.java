package com.ly.fn.motanx.api.rpc;

/**
 * node manage interface
 */
public interface Node {

    void init();

    void destroy();

    boolean isAvailable();

    String desc();

    URL getUrl();
}