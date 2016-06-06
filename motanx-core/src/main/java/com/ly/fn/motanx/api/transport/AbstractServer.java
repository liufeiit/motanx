package com.ly.fn.motanx.api.transport;

import java.net.InetSocketAddress;
import java.util.Collection;

import com.ly.fn.motanx.api.codec.Codec;
import com.ly.fn.motanx.api.common.ChannelState;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.rpc.URL;

public abstract class AbstractServer implements Server {
    protected InetSocketAddress localAddress;
    protected InetSocketAddress remoteAddress;

    protected URL url;
    protected Codec codec;

    protected volatile ChannelState state = ChannelState.UNINIT;


    public AbstractServer() {}

    public AbstractServer(URL url) {
        this.url = url;
        this.codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(url.getParameter(URLParamType.codec.getName(), URLParamType.codec.getValue()));
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public Collection<Channel> getChannels() {
        throw new MotanxFrameworkException(this.getClass().getName() + " getChannels() method unsupport " + url);
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        throw new MotanxFrameworkException(this.getClass().getName() + " getChannel(InetSocketAddress) method unsupport " + url);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setCodec(Codec codec) {
        this.codec = codec;
    }

}
