package com.ly.fn.motanx.api.transport;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;
import com.ly.fn.motanx.api.rpc.URL;

@Spi(scope = Scope.SINGLETON)
public interface EndpointFactory {

    /**
     * create remote server
     * 
     * @param url
     * @param messageHandler
     * @return
     */
    Server createServer(URL url, MessageHandler messageHandler);

    /**
     * create remote client
     * 
     * @param url
     * @return
     */
    Client createClient(URL url);

    /**
     * safe release server
     * 
     * @param server
     * @param url
     */
    void safeReleaseResource(Server server, URL url);

    /**
     * safe release client
     * 
     * @param client
     * @param url
     */
    void safeReleaseResource(Client client, URL url);

}
