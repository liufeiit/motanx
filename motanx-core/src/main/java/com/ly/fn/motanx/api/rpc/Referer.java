package com.ly.fn.motanx.api.rpc;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;

/**
 * Refer to a service.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Referer<T> extends Caller<T>, Node {

    /**
     * 当前使用该referer的调用数
     * 
     * @return
     */
    int activeRefererCount();

    /**
     * 获取referer的原始service url
     * 
     * @return
     */
    URL getServiceUrl();
}
