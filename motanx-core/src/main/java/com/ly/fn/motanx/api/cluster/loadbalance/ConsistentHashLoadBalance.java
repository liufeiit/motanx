package com.ly.fn.motanx.api.cluster.loadbalance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;

@SpiMeta(name = "consistent")
public class ConsistentHashLoadBalance<T> extends AbstractLoadBalance<T> {

    private List<Referer<T>> consistentHashReferers;

    @Override
    public void onRefresh(List<Referer<T>> referers) {
        super.onRefresh(referers);

        List<Referer<T>> copyReferers = new ArrayList<Referer<T>>(referers);
        List<Referer<T>> tempRefers = new ArrayList<Referer<T>>();
        for (int i = 0; i < MotanxConstants.DEFAULT_CONSISTENT_HASH_BASE_LOOP; i++) {
            Collections.shuffle(copyReferers);
            for (Referer<T> ref : copyReferers) {
                tempRefers.add(ref);
            }
        }

        consistentHashReferers = tempRefers;
    }

    @Override
    protected Referer<T> doSelect(Request request) {

        int hash = getHash(request);
        Referer<T> ref;
        for (int i = 0; i < getReferers().size(); i++) {
            ref = consistentHashReferers.get((hash + i) % consistentHashReferers.size());
            if (ref.isAvailable()) {
                return ref;
            }
        }
        return null;
    }

    @Override
    protected void doSelectToHolder(Request request, List<Referer<T>> refersHolder) {
        List<Referer<T>> referers = getReferers();

        int hash = getHash(request);
        for (int i = 0; i < referers.size(); i++) {
            Referer<T> ref = consistentHashReferers.get((hash + i) % consistentHashReferers.size());
            if (ref.isAvailable()) {
                refersHolder.add(ref);
            }
        }
    }

    private int getHash(Request request) {
        if (request.getArguments() == null || request.getArguments().length == 0) {
            return request.hashCode();
        } else {
            return Arrays.hashCode(request.getArguments());
        }
    }


}
