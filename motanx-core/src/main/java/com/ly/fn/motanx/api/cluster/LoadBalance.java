package com.ly.fn.motanx.api.cluster;

import java.util.List;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;

/**
 * Loadbalance for select referer
 */
@Spi(scope = Scope.PROTOTYPE)
public interface LoadBalance<T> {

    void onRefresh(List<Referer<T>> referers);

    Referer<T> select(Request request);

    void selectToHolder(Request request, List<Referer<T>> refersHolder);

    void setWeightString(String weightString);

}
