package com.ly.fn.motanx.api.transport;

public interface EndpointManager {

    void init();

    void destroy();

    void addEndpoint(Endpoint endpoint);

    void removeEndpoint(Endpoint endpoint);

}
