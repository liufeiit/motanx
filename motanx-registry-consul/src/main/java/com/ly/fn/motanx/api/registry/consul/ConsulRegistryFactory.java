package com.ly.fn.motanx.api.registry.consul;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.registry.Registry;
import com.ly.fn.motanx.api.registry.consul.client.ConsulEcwidClient;
import com.ly.fn.motanx.api.registry.consul.client.MotanxConsulClient;
import com.ly.fn.motanx.api.registry.support.AbstractRegistryFactory;
import com.ly.fn.motanx.api.rpc.URL;


@SpiMeta(name = "consul")
public class ConsulRegistryFactory extends AbstractRegistryFactory {

	@Override
	protected Registry createRegistry(URL url) {
	    String host = ConsulConstants.DEFAULT_HOST;
        int port = ConsulConstants.DEFAULT_PORT;
        if (StringUtils.isNotBlank(url.getHost())) {
            host = url.getHost();
        }
        if (url.getPort() > 0) {
            port = url.getPort();
        }
        //可以使用不同的client实现
        MotanxConsulClient client = new ConsulEcwidClient(host, port);
		return new ConsulRegistry(url, client);
	}

}
