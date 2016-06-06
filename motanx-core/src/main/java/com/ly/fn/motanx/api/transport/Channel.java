package com.ly.fn.motanx.api.transport;

import java.net.InetSocketAddress;

import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;

public interface Channel {

    /**
     * get local socket address.
     * 
     * @return local address.
     */
    InetSocketAddress getLocalAddress();

    /**
     * get remote socket address
     * 
     * @return
     */
    InetSocketAddress getRemoteAddress();

    /**
     * send request.
     *
     * @param request
     * @return response future
     * @throws TransportException
     */
    Response request(Request request) throws TransportException;

    /**
     * open the channel
     * 
     * @return
     */
    boolean open();

    /**
     * close the channel.
     */
    void close();

    /**
     * close the channel gracefully.
     */
    void close(int timeout);

    /**
     * is closed.
     * 
     * @return closed
     */
    boolean isClosed();

    /**
     * the node available status
     * 
     * @return
     */
    boolean isAvailable();

    /**
     * 
     * @return
     */
    URL getUrl();

}
