package com.ly.fn.motanx.api.rpc;

import java.util.concurrent.atomic.AtomicInteger;

import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

public abstract class AbstractReferer<T> extends AbstractNode implements Referer<T> {

    protected Class<T> clz;
    protected AtomicInteger activeRefererCount = new AtomicInteger(0);
    protected URL serviceUrl;

    public AbstractReferer(Class<T> clz, URL url) {
        super(url);
        this.clz = clz;
        this.serviceUrl = url;
    }

    public AbstractReferer(Class<T> clz, URL url, URL serviceUrl) {
        super(url);
        this.clz = clz;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response call(Request request) {
        if (!isAvailable()) {
            throw new MotanxFrameworkException(this.getClass().getSimpleName() + " call Error: node is not available, url=" + url.getUri() + " " + MotanxFrameworkUtil.toString(request));
        }

        incrActiveCount(request);
        Response response = null;
        try {
            response = doCall(request);

            return response;
        } finally {
            decrActiveCount(request, response);
        }
    }

    @Override
    public int activeRefererCount() {
        return activeRefererCount.get();
    }

    protected void incrActiveCount(Request request) {
        activeRefererCount.incrementAndGet();
    }

    protected void decrActiveCount(Request request, Response response) {
        activeRefererCount.decrementAndGet();
    }

    protected abstract Response doCall(Request request);

    @Override
    public String desc() {
        return "[" + this.getClass().getSimpleName() + "] url=" + url;
    }

    @Override
    public URL getServiceUrl() {
        return serviceUrl;
    }

}
