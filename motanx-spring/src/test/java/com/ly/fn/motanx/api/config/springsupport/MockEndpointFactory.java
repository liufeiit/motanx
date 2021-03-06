/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ly.fn.motanx.api.config.springsupport;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Client;
import com.ly.fn.motanx.api.transport.MessageHandler;
import com.ly.fn.motanx.api.transport.Server;
import com.ly.fn.motanx.api.transport.support.AbstractEndpointFactory;

/**
 * 
 * @Description MockEndpointFactory
 * @author zhanglei28
 * @date 2016年3月17日
 *
 */

@SpiMeta(name = "mockEndpoint")
public class MockEndpointFactory extends AbstractEndpointFactory {
    Server server = null;
    Client client = null;

    @Override
    protected Server innerCreateServer(URL url, MessageHandler messageHandler) {
        return server;
    }

    @Override
    protected Client innerCreateClient(URL url) {
        return client;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public Server createServer(URL url, MessageHandler messageHandler) {
        return innerCreateServer(url, messageHandler);
    }


}
