package com.ly.fn.motanx.api.transport;

import com.ly.fn.motanx.api.rpc.Request;

public interface Client extends Endpoint {
    /**
     * async send request.
     *
     * @param request
     */
    void heartbeat(Request request);
}
