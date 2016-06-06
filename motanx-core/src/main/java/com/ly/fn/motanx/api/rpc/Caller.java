package com.ly.fn.motanx.api.rpc;

public interface Caller<T> extends Node {

    Class<T> getInterface();

    Response call(Request request);
}
