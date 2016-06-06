package com.ly.fn.motanx.api.cluster.support;

import com.ly.fn.motanx.api.cluster.Cluster;
import com.ly.fn.motanx.api.cluster.HaStrategy;
import com.ly.fn.motanx.api.cluster.LoadBalance;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxAbstractException;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.*;
import com.ly.fn.motanx.api.util.CollectionUtil;
import com.ly.fn.motanx.api.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Cluster spi.
 */
@SpiMeta(name = "default")
public class ClusterSpi<T> implements Cluster<T> {

    private HaStrategy<T> haStrategy;

    private LoadBalance<T> loadBalance;

    private List<Referer<T>> referers;

    private AtomicBoolean available = new AtomicBoolean(false);

    private URL url;

    @Override
    public void init() {
        onRefresh(referers);
        available.set(true);
    }

    @Override
    public Class<T> getInterface() {
        if (referers == null || referers.isEmpty()) {
            return null;
        }

        return referers.get(0).getInterface();
    }

    @Override
    public Response call(Request request) {
        if (available.get()) {
            try {
                return haStrategy.call(request, loadBalance);
            } catch (Exception e) {
                return callFalse(request, e);
            }
        }
        return callFalse(request, new MotanxServiceException(MotanxErrorMsgConstant.SERVICE_UNFOUND));
    }

    @Override
    public String desc() {
        return toString();
    }

    @Override
    public void destroy() {
        available.set(false);
        for (Referer<T> referer : this.referers) {
            referer.destroy();
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public boolean isAvailable() {
        return available.get();
    }

    @Override
    public String toString() {
        return "cluster: {" + "ha=" + haStrategy + ",loadbalance=" + loadBalance + "referers=" + referers + "}";

    }

    @Override
    public synchronized void onRefresh(List<Referer<T>> referers) {
        if (CollectionUtil.isEmpty(referers)) {
            return;
        }

        loadBalance.onRefresh(referers);
        List<Referer<T>> oldReferers = this.referers;
        this.referers = referers;
        haStrategy.setUrl(getUrl());

        if (oldReferers == null || oldReferers.isEmpty()) {
            return;
        }

        List<Referer<T>> delayDestroyReferers = new ArrayList<Referer<T>>();

        for (Referer<T> referer : oldReferers) {
            if (referers.contains(referer)) {
                continue;
            }

            delayDestroyReferers.add(referer);
        }

        if (!delayDestroyReferers.isEmpty()) {
            RefererSupports.delayDestroy(delayDestroyReferers);
        }
    }

    public AtomicBoolean getAvailable() {
        return available;
    }

    public void setAvailable(AtomicBoolean available) {
        this.available = available;
    }

    public HaStrategy<T> getHaStrategy() {
        return haStrategy;
    }

    @Override
    public void setHaStrategy(HaStrategy<T> haStrategy) {
        this.haStrategy = haStrategy;
    }

    @Override
    public LoadBalance<T> getLoadBalance() {
        return loadBalance;
    }

    @Override
    public void setLoadBalance(LoadBalance<T> loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public List<Referer<T>> getReferers() {
        return referers;
    }

    protected Response callFalse(Request request, Exception cause) {

        // biz exception 无论如何都要抛出去
        if (ExceptionUtil.isBizException(cause)) {
            throw (RuntimeException) cause;
        }

        // 其他异常根据配置决定是否抛，如果抛异常，需要统一为MotanException
        if (Boolean.parseBoolean(getUrl().getParameter(URLParamType.throwException.getName(), URLParamType.throwException.getValue()))) {
            if (cause instanceof MotanxAbstractException) {
                throw (MotanxAbstractException) cause;
            } else {
                MotanxServiceException motanException = new MotanxServiceException(String.format("ClusterSpi Call false for request: %s", request), cause);
                throw motanException;
            }
        }

        return buildErrorResponse(request, cause);
    }

    private Response buildErrorResponse(Request request, Exception motanException) {
        DefaultResponse rs = new DefaultResponse();
        rs.setException(motanException);
        rs.setRequestId(request.getRequestId());
        return rs;
    }

}
