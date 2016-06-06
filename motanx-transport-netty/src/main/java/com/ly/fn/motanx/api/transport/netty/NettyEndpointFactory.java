package com.ly.fn.motanx.api.transport.netty;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Client;
import com.ly.fn.motanx.api.transport.MessageHandler;
import com.ly.fn.motanx.api.transport.Server;
import com.ly.fn.motanx.api.transport.netty.NettyClient;
import com.ly.fn.motanx.api.transport.netty.NettyServer;
import com.ly.fn.motanx.api.transport.support.AbstractEndpointFactory;

@SpiMeta(name = "motanx")
public class NettyEndpointFactory extends AbstractEndpointFactory {

    @Override
    protected Server innerCreateServer(URL url, MessageHandler messageHandler) {
        return new NettyServer(url, messageHandler);
    }

    @Override
    protected Client innerCreateClient(URL url) {
        return new NettyClient(url);
    }
}