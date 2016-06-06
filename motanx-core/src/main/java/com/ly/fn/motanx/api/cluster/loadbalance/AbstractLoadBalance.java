package com.ly.fn.motanx.api.cluster.loadbalance;

import java.util.List;

import com.ly.fn.motanx.api.cluster.LoadBalance;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {

    private List<Referer<T>> referers;

    @Override
    public void onRefresh(List<Referer<T>> referers) {
        // 只能引用替换，不能进行referers update。
        this.referers = referers;
    }

    @Override
    public Referer<T> select(Request request) {
        List<Referer<T>> referers = this.referers;

        Referer<T> ref = null;
        if (referers.size() > 1) {
            ref = doSelect(request);

        } else if (referers.size() == 1) {
            ref = referers.get(0).isAvailable() ? referers.get(0) : null;
        }

        if (ref != null) {
            return ref;
        }
        throw new MotanxServiceException(this.getClass().getSimpleName() + " No available referers for call request:" + request);
    }

    @Override
    public void selectToHolder(Request request, List<Referer<T>> refersHolder) {
        List<Referer<T>> referers = this.referers;

        if (referers == null) {
            throw new MotanxServiceException(this.getClass().getSimpleName() + " No available referers for call : referers_size= 0 " + MotanxFrameworkUtil.toString(request));
        }

        if (referers.size() > 1) {
            doSelectToHolder(request, refersHolder);

        } else if (referers.size() == 1 && referers.get(0).isAvailable()) {
            refersHolder.add(referers.get(0));
        }
        if (refersHolder.isEmpty()) {
            throw new MotanxServiceException(this.getClass().getSimpleName() + " No available referers for call : referers_size=" + referers.size() + " " + MotanxFrameworkUtil.toString(request));
        }
    }

    protected List<Referer<T>> getReferers() {
        return referers;
    }

    @Override
    public void setWeightString(String weightString) {
        LoggerUtil.info("ignore weightString:" + weightString);
    }

    protected abstract Referer<T> doSelect(Request request);

    protected abstract void doSelectToHolder(Request request, List<Referer<T>> refersHolder);
}
