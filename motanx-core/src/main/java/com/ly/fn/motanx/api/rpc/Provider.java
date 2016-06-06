package com.ly.fn.motanx.api.rpc;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;

/**
 * Service provider.
 */
@Spi(scope = Scope.PROTOTYPE)
public interface Provider<T> extends Caller<T> {

    Class<T> getInterface();
}
