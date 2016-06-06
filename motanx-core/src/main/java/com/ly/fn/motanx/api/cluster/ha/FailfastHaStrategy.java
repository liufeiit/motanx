package com.ly.fn.motanx.api.cluster.ha;

import com.ly.fn.motanx.api.cluster.LoadBalance;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;

@SpiMeta(name = "failfast")
public class FailfastHaStrategy<T> extends AbstractHaStrategy<T> {

    @Override
    public Response call(Request request, LoadBalance<T> loadBalance) {
        Referer<T> refer = loadBalance.select(request);
        return refer.call(request);
    }
}
