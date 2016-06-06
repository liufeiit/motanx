package com.ly.fn.motanx.api.registry;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.URL;

@Spi(scope = Scope.SINGLETON)
public interface RegistryFactory {

    Registry getRegistry(URL url);
}
