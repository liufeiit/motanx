package com.ly.fn.motanx.api.transport.netty;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.ly.fn.motanx.api.util.LoggerUtil;

public class NettyServerChannelManage extends SimpleChannelHandler {
    private ConcurrentMap<String, Channel> channels = new ConcurrentHashMap<String, Channel>();

    private int maxChannel = 0;

    public NettyServerChannelManage(int maxChannel) {
        super();
        this.maxChannel = maxChannel;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();

        String channelKey = getChannelKey((InetSocketAddress) channel.getLocalAddress(), (InetSocketAddress) channel.getRemoteAddress());

        if (channels.size() > maxChannel) {
            // 超过最大连接数限制，直接close连接
            LoggerUtil.warn("NettyServerChannelManage channelConnected channel size out of limit: limit={} current={}", maxChannel, channels.size());

            channel.close();
        } else {
            channels.put(channelKey, channel);
            ctx.sendUpstream(e);
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Channel channel = ctx.getChannel();

        String channelKey = getChannelKey((InetSocketAddress) channel.getLocalAddress(), (InetSocketAddress) channel.getRemoteAddress());

        channels.remove(channelKey);
        ctx.sendUpstream(e);
    }

    public Map<String, Channel> getChannels() {
        return channels;
    }

    /**
     * close所有的连接
     */
    public void close() {
        for (Map.Entry<String, Channel> entry : channels.entrySet()) {
            try {
                Channel channel = entry.getValue();

                if (channel != null) {
                    channel.close();
                }
            } catch (Exception e) {
                LoggerUtil.error("NettyServerChannelManage close channel Error: " + entry.getKey(), e);
            }
        }
    }

    /**
     * remote address + local address 作为连接的唯一标示
     * 
     * @param local
     * @param remote
     * @return
     */
    private String getChannelKey(InetSocketAddress local, InetSocketAddress remote) {
        String key = "";
        if (local == null || local.getAddress() == null) {
            key += "null-";
        } else {
            key += local.getAddress().getHostAddress() + ":" + local.getPort() + "-";
        }

        if (remote == null || remote.getAddress() == null) {
            key += "null";
        } else {
            key += remote.getAddress().getHostAddress() + ":" + remote.getPort();
        }

        return key;
    }
}
