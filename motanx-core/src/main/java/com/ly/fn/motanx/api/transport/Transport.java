package com.ly.fn.motanx.api.transport;

import java.util.concurrent.Future;

public interface Transport {

    /**
     * remote transport
     * 
     * @return
     */
    Future<byte[]> transport(byte[] request) throws TransportException;

    /**
     * 判断transport的available状态
     * 
     * @return
     */
    boolean isAvailable();

    /**
     * 判断transport的connect状态
     * 
     * @return
     */
    boolean isConnect();

    /**
     * transport connect
     */
    boolean connect();

    /**
     * close transport
     */
    void close();

    /**
     * close transport
     */
    void close(int timeout);

    /**
     * transport is close?
     * 
     * @return
     */
    boolean isClose();
}
