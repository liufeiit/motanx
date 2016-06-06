package com.ly.fn.motanx.api.cluster;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;

/**
 * Ha strategy.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface HaStrategy<T> {

    void setUrl(URL url);

    Response call(Request request, LoadBalance<T> loadBalance);

}
