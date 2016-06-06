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

package com.ly.fn.motanx.api.filter;

import com.ly.fn.motanx.api.BaseTestCase;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.filter.AccessLogFilter;
import com.ly.fn.motanx.api.protocol.example.IHello;
import com.ly.fn.motanx.api.registry.RegistryService;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.NetUtils;

import org.jmock.Expectations;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-28
 */

public class AccessLogFilterTest extends BaseTestCase {

    private AccessLogFilter accessLogFilter = new AccessLogFilter();

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testCall() {
        final Request request = mockery.mock(Request.class);
        final Response response = mockery.mock(Response.class);
        final URL url =
                new URL(MotanxConstants.PROTOCOL_MOTANX, NetUtils.getLocalAddress().getHostAddress(), 0, RegistryService.class.getName());
        url.addParameter(URLParamType.accessLog.getName(), String.valueOf(true));

        final Caller<IHello> caller = mockery.mock(Caller.class);
        final Map<String, String> attachments = new HashMap<String, String>();
        attachments.put(URLParamType.host.getName(), URLParamType.host.getValue());
        attachments.put(URLParamType.application.getName(), URLParamType.application.getValue());
        attachments.put(URLParamType.module.getName(), URLParamType.module.getValue());

        mockery.checking(new Expectations() {
            {
                atLeast(1).of(caller).getUrl();
                will(returnValue(url));
                exactly(1).of(caller).call(request);
                will(returnValue(response));
                exactly(1).of(request).getInterfaceName();
                will(returnValue(IHello.class.getName()));
                exactly(1).of(request).getMethodName();
                will(returnValue("get"));
                exactly(1).of(request).getParamtersDesc();
                will(returnValue("param_desc"));
                exactly(1).of(request).getRequestId();
                will(returnValue(100L));
                atLeast(1).of(request).getAttachments();
                will(returnValue(attachments));
            }
        });

        accessLogFilter.filter(caller, request);
    }
}
