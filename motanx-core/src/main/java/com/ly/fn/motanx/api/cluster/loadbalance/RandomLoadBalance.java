package com.ly.fn.motanx.api.cluster.loadbalance;

import java.util.List;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;

@SpiMeta(name = "random")
public class RandomLoadBalance<T> extends AbstractLoadBalance<T> {

    @Override
    protected Referer<T> doSelect(Request request) {
        List<Referer<T>> referers = getReferers();

        int idx = (int) (Math.random() * referers.size());
        for (int i = 0; i < referers.size(); i++) {
            Referer<T> ref = referers.get((i + idx) % referers.size());
            if (ref.isAvailable()) {
                return ref;
            }
        }
        return null;
    }

    @Override
    protected void doSelectToHolder(Request request, List<Referer<T>> refersHolder) {
        List<Referer<T>> referers = getReferers();

        int idx = (int) (Math.random() * referers.size());
        for (int i = 0; i < referers.size(); i++) {
            Referer<T> referer = referers.get((i + idx) % referers.size());
            if (referer.isAvailable()) {
                refersHolder.add(referers.get((i + idx) % referers.size()));
            }
        }
    }
}
