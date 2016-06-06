package com.ly.fn.motanx.api.transport.netty;

import org.apache.commons.pool.BasePoolableObjectFactory;

import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Channel;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class NettyChannelFactory extends BasePoolableObjectFactory<Channel> {
    private String factoryName = "";
    private NettyClient nettyClient;

    public NettyChannelFactory(NettyClient nettyClient) {
        super();

        this.nettyClient = nettyClient;
        this.factoryName = "NettyChannelFactory_" + nettyClient.getUrl().getHost() + "_" + nettyClient.getUrl().getPort();
    }

    public String getFactoryName() {
        return factoryName;
    }

    @Override
    public String toString() {
        return factoryName;
    }

    @Override
    public Channel makeObject() throws Exception {
        NettyChannel nettyChannel = new NettyChannel(nettyClient);
        nettyChannel.open();
        return nettyChannel;
    }

    @Override
    public void destroyObject(final Channel obj) throws Exception {
        if (obj instanceof NettyChannel) {
            NettyChannel client = (NettyChannel) obj;
            URL url = nettyClient.getUrl();

            try {
                client.close();

                LoggerUtil.info(factoryName + " client disconnect Success: " + url.getUri());
            } catch (Exception e) {
                LoggerUtil.error(factoryName + " client disconnect Error: " + url.getUri(), e);
            }
        }
    }

    @Override
    public boolean validateObject(final Channel obj) {
        if (obj instanceof NettyChannel) {
            final NettyChannel client = (NettyChannel) obj;
            try {
                return client.isAvailable();
            } catch (final Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void activateObject(Channel obj) throws Exception {
        if (obj instanceof NettyChannel) {
            final NettyChannel client = (NettyChannel) obj;
            if (!client.isAvailable()) {
                client.open();
            }
        }
    }

    @Override
    public void passivateObject(Channel obj) throws Exception {}
}
