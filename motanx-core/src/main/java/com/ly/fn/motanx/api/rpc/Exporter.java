package com.ly.fn.motanx.api.rpc;

/**
 * Export service providers.
 */
public interface Exporter<T> extends Node {

    Provider<T> getProvider();

    void unexport();
}
