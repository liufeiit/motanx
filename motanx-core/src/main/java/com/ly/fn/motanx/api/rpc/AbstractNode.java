package com.ly.fn.motanx.api.rpc;

import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.util.LoggerUtil;

public abstract class AbstractNode implements Node {

    protected URL url;

    protected volatile boolean init = false;
    protected volatile boolean available = false;

    public AbstractNode(URL url) {
        this.url = url;
    }

    @Override
    public synchronized void init() {
        if (init) {
            LoggerUtil.warn(this.getClass().getSimpleName() + " node already init: " + desc());
            return;
        }
        boolean result = doInit();
        if (!result) {
            LoggerUtil.error(this.getClass().getSimpleName() + " node init Error: " + desc());
            throw new MotanxFrameworkException(this.getClass().getSimpleName() + " node init Error: " + desc(), MotanxErrorMsgConstant.FRAMEWORK_INIT_ERROR);
        } else {
            LoggerUtil.info(this.getClass().getSimpleName() + " node init Success: " + desc());
            init = true;
            available = true;
        }
    }

    protected abstract boolean doInit();

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public URL getUrl() {
        return url;
    }
}
