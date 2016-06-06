package com.ly.fn.motanx.api.config.springsupport;

import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月12日 下午4:24:40
 */
public class RpcServiceBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RpcServiceExporter.class;
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }
}
