package com.ly.fn.motanx.api.cluster.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;

@SpiMeta(name = "roundrobin")
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    protected Referer<T> doSelect(Request request) {
        List<Referer<T>> referers = getReferers();

        int index = idx.incrementAndGet();
        for (int i = 0; i < referers.size(); i++) {
            Referer<T> ref = referers.get((i + index) % referers.size());
            if (ref.isAvailable()) {
                return ref;
            }
        }
        return null;
    }

    @Override
    protected void doSelectToHolder(Request request, List<Referer<T>> refersHolder) {
        List<Referer<T>> referers = getReferers();

        int index = idx.incrementAndGet();
        for (int i = 0; i < referers.size(); i++) {
            Referer<T> referer = referers.get((i + index) % referers.size());
            if (referer.isAvailable()) {
                refersHolder.add(referer);
            }
        }
    }
}
