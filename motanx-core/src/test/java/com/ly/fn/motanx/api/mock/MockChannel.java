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

package com.ly.fn.motanx.api.mock;

import java.net.InetSocketAddress;

import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Channel;
import com.ly.fn.motanx.api.transport.TransportException;

/**
 * @author maijunsheng
 * @version 创建时间：2013-6-5
 * 
 */
public class MockChannel implements Channel {
    private URL url;

    public MockChannel(URL url) {
        this.url = url;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public Response request(Request request) throws TransportException {
        return null;
    }

    @Override
    public boolean open() {
        return false;
    }

    @Override
    public void close() {}

    @Override
    public void close(int timeout) {}

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public URL getUrl() {
        return url;
    }

}
