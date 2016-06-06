package com.ly.fn.motanx.api.rpc;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;

/**
 * protocol
 * 
 * <pre>
 * 只负责点到点的通讯
 * </pre>
 */
@Spi(scope = Scope.SINGLETON)
public interface Protocol {
    /**
     * 暴露服务
     * 
     * @param <T>
     * @param provider
     * @param url
     * @return
     */
    <T> Exporter<T> export(Provider<T> provider, URL url);

    /**
     * 引用服务
     * 
     * @param <T>
     * @param clz
     * @param url
     * @param serviceUrl
     * @return
     */
    <T> Referer<T> refer(Class<T> clz, URL url, URL serviceUrl);

    /**
     * <pre>
	 * 		1） exporter destroy
	 * 		2） referer destroy
	 * </pre>
     * 
     */
    void destroy();
}
