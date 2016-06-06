package com.ly.fn.motanx.api.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

public interface Server extends Endpoint {

    /**
     * is server bound
     * 
     * @return
     */
    boolean isBound();

    /**
     * get channels.
     * 
     * @return channels
     */
    Collection<Channel> getChannels();

    /**
     * get channel.
     * 
     * @param remoteAddress
     * @return channel
     */
    Channel getChannel(InetSocketAddress remoteAddress);
}
