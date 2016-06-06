package com.ly.fn.motanx.api.cluster;

import java.util.List;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.URL;

/**
 * Cluster is a service broker, used to
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Cluster<T> extends Caller<T> {

    @Override
    void init();

    void setUrl(URL url);

    void setLoadBalance(LoadBalance<T> loadBalance);

    void setHaStrategy(HaStrategy<T> haStrategy);

    void onRefresh(List<Referer<T>> referers);

    List<Referer<T>> getReferers();

    LoadBalance<T> getLoadBalance();
}
