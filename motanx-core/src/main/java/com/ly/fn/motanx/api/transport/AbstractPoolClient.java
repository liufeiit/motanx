package com.ly.fn.motanx.api.transport;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.LoggerUtil;

public abstract class AbstractPoolClient extends AbstractClient {
    protected static long defaultMinEvictableIdleTimeMillis = (long) 1000 * 60 * 60;// 默认链接空闲时间
    protected static long defaultSoftMinEvictableIdleTimeMillis = (long) 1000 * 60 * 10;//
    protected static long defaultTimeBetweenEvictionRunsMillis = (long) 1000 * 60 * 10;// 默认回收周期
    protected GenericObjectPool<Channel> pool;
    protected GenericObjectPool.Config poolConfig;
    protected PoolableObjectFactory<Channel> factory;

    public AbstractPoolClient(URL url) {
        super(url);
    }

    protected void initPool() {
        poolConfig = new GenericObjectPool.Config();
        poolConfig.minIdle = url.getIntParameter(URLParamType.minClientConnection.getName(), URLParamType.minClientConnection.getIntValue());
        poolConfig.maxIdle = url.getIntParameter(URLParamType.maxClientConnection.getName(), URLParamType.maxClientConnection.getIntValue());
        poolConfig.maxActive = poolConfig.maxIdle;
        poolConfig.maxWait = url.getIntParameter(URLParamType.requestTimeout.getName(), URLParamType.requestTimeout.getIntValue());
        poolConfig.lifo = url.getBooleanParameter(URLParamType.poolLifo.getName(), URLParamType.poolLifo.getBooleanValue());
        poolConfig.minEvictableIdleTimeMillis = defaultMinEvictableIdleTimeMillis;
        poolConfig.softMinEvictableIdleTimeMillis = defaultSoftMinEvictableIdleTimeMillis;
        poolConfig.timeBetweenEvictionRunsMillis = defaultTimeBetweenEvictionRunsMillis;
        factory = createChannelFactory();

        pool = new GenericObjectPool<Channel>(factory, poolConfig);

        boolean lazyInit = url.getBooleanParameter(URLParamType.lazyInit.getName(), URLParamType.lazyInit.getBooleanValue());

        if (!lazyInit) {
            for (int i = 0; i < poolConfig.minIdle; i++) {
                try {
                    pool.addObject();
                } catch (Exception e) {
                    LoggerUtil.error("NettyClient init pool create connect Error: url=" + url.getUri(), e);
                }
            }
        }
    }

    protected abstract BasePoolableObjectFactory<Channel> createChannelFactory();

    protected Channel borrowObject() throws Exception {
        Channel nettyChannel = (Channel) pool.borrowObject();

        if (nettyChannel != null && nettyChannel.isAvailable()) {
            return nettyChannel;
        }

        invalidateObject(nettyChannel);

        String errorMsg = this.getClass().getSimpleName() + " borrowObject Error: url=" + url.getUri();
        LoggerUtil.error(errorMsg);
        throw new MotanxServiceException(errorMsg);
    }

    protected void invalidateObject(Channel nettyChannel) {
        if (nettyChannel == null) {
            return;
        }
        try {
            pool.invalidateObject(nettyChannel);
        } catch (Exception ie) {
            LoggerUtil.error(this.getClass().getSimpleName() + " invalidate client Error: url=" + url.getUri(), ie);
        }
    }

    protected void returnObject(Channel channel) {
        if (channel == null) {
            return;
        }

        try {
            pool.returnObject(channel);
        } catch (Exception ie) {
            LoggerUtil.error(this.getClass().getSimpleName() + " return client Error: url=" + url.getUri(), ie);
        }
    }

}
