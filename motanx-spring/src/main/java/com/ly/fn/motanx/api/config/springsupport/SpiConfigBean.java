package com.ly.fn.motanx.api.config.springsupport;

import org.springframework.beans.factory.InitializingBean;

import com.ly.fn.motanx.api.config.SpiConfig;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;

public class SpiConfigBean<T> extends SpiConfig<T> implements InitializingBean {

    @Override
    public final void afterPropertiesSet() throws Exception {
        ExtensionLoader.getExtensionLoader(getInterfaceClass()).addExtensionClass(getSpiClass());
    }
}