package com.ly.fn.motanx.api.registry.support;

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.registry.Registry;
import com.ly.fn.motanx.api.rpc.URL;

@SpiMeta(name = "local")
public class LocalRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(URL url) {
        return ExtensionLoader.getExtensionLoader(Registry.class).getExtension(MotanxConstants.REGISTRY_PROTOCOL_LOCAL);
    }
}
