package com.ly.fn.motanx.api.transport;

import java.net.InetSocketAddress;

import com.ly.fn.motanx.api.codec.Codec;
import com.ly.fn.motanx.api.common.ChannelState;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

public abstract class AbstractClient implements Client {

    protected InetSocketAddress localAddress;
    protected InetSocketAddress remoteAddress;

    protected URL url;
    protected Codec codec;

    protected volatile ChannelState state = ChannelState.UNINIT;

    public AbstractClient(URL url) {
        this.url = url;
        this.codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(url.getParameter(URLParamType.codec.getName(), URLParamType.codec.getValue()));
        LoggerUtil.info("init nettyclient. url:" + url.getHost() + "-" + url.getPath() + ", use codec:" + codec.getClass().getSimpleName());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public void heartbeat(Request request) {
        throw new MotanxFrameworkException("heartbeat not support: " + MotanxFrameworkUtil.toString(request));
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
