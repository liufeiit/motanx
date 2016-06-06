package com.ly.fn.motanx.api.registry;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.URL;

/**
 * Used to register and discover.
 */
@Spi(scope = Scope.SINGLETON)
public interface Registry extends RegistryService, DiscoveryService {

    URL getUrl();
}
