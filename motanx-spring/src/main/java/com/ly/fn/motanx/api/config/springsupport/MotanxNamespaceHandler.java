package com.ly.fn.motanx.api.config.springsupport;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.ly.fn.motanx.api.config.BasicRefererInterfaceConfig;
import com.ly.fn.motanx.api.config.BasicServiceInterfaceConfig;
import com.ly.fn.motanx.api.config.ProtocolConfig;
import com.ly.fn.motanx.api.config.RegistryConfig;

public class MotanxNamespaceHandler extends NamespaceHandlerSupport {
    
    @Override
    public void init() {
        registerBeanDefinitionParser("referer", new MotanxBeanDefinitionParser(RefererConfigBean.class, false));
        registerBeanDefinitionParser("service", new MotanxBeanDefinitionParser(ServiceConfigBean.class, true));
        registerBeanDefinitionParser("protocol", new MotanxBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("registry", new MotanxBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("basicService", new MotanxBeanDefinitionParser(BasicServiceInterfaceConfig.class, true));
        registerBeanDefinitionParser("basicReferer", new MotanxBeanDefinitionParser(BasicRefererInterfaceConfig.class, true));
        registerBeanDefinitionParser("spi", new MotanxBeanDefinitionParser(SpiConfigBean.class, true));
        registerBeanDefinitionParser("annotation-driven", new RpcwiredBeanDefinitionParser());
        registerBeanDefinitionParser("service-exporter", new RpcServiceBeanDefinitionParser());
    }
}
