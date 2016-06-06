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

package com.ly.fn.motanx.api.protocol.rpc;

import org.junit.Before;
import org.junit.Test;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.protocol.example.Hello;
import com.ly.fn.motanx.api.protocol.example.IHello;
import com.ly.fn.motanx.api.rpc.DefaultRequest;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Provider;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;

import junit.framework.Assert;

/**
 * @author maijunsheng
 * @version 创建时间：2013-5-23
 */
public class DefaultRpcProtocolTest {

    private DefaultRpcProtocol defaultRpcProtocol;
    private URL url;

    @Before
    public void setUp() {
        defaultRpcProtocol = new DefaultRpcProtocol();
    }

    @Test
    public void testProtocol() {
        url = new URL("motan", "localhost", 18080, "com.weibo.api.motan.procotol.example.IHello");
        url.getParameters().put(URLParamType.endpointFactory.getName(), "mockEndpoint");
        try {
            defaultRpcProtocol.export(null, null);
        } catch (Exception e) {
            if (e instanceof MotanxFrameworkException) {
                Assert.assertTrue(e.getMessage().contains("url is null"));
            } else {
                Assert.assertTrue(false);
            }
        }
        try {
            defaultRpcProtocol.export(null, url);
        } catch (Exception e) {
            if (e instanceof MotanxFrameworkException) {
                Assert.assertTrue(e.getMessage().contains("provider is null"));
            } else {
                Assert.assertTrue(false);
            }
        }

        defaultRpcProtocol.export(new Provider<IHello>() {
            @Override
            public Response call(Request request) {
                IHello hello = new Hello();
                hello.hello();
                return new DefaultResponse("hello");
            }

            @Override
            public void init() {
            }

            @Override
            public void destroy() {
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public String desc() {
                return null;
            }

            @Override
            public URL getUrl() {
                return new URL("motan", "localhost", 18080, "com.weibo.api.motan.procotol.example.IHello");
            }

            @Override
            public Class<IHello> getInterface() {
                return IHello.class;
            }

        }, url);

        Referer<IHello> referer = defaultRpcProtocol.refer(IHello.class, url);

        DefaultRequest request = new DefaultRequest();
        request.setMethodName("hello");
        request.setInterfaceName(IHello.class.getName());

        Response response = referer.call(request);

        System.out.println("client: " + response.getValue());

        defaultRpcProtocol.destroy();
    }


}
