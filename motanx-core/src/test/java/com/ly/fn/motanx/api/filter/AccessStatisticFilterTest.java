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

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;

import com.ly.fn.motanx.api.BaseTestCase;
import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.exception.MotanxBizException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.filter.AccessStatisticFilter;
import com.ly.fn.motanx.api.protocol.example.IHello;
import com.ly.fn.motanx.api.registry.RegistryService;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.NetUtils;


/**
 * 
 * test
 *
 * @author fishermen
 * @version V1.0 created at: 2013-6-28
 */

public class AccessStatisticFilterTest extends BaseTestCase {

    private AccessStatisticFilter accessStatisticFilter = new AccessStatisticFilter();

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testFilter() {
        final Request request = mockery.mock(Request.class);
        final Response response = mockery.mock(Response.class);
        final Caller<IHello> caller = mockery.mock(Caller.class);
        final URL url =
                new URL(MotanxConstants.PROTOCOL_MOTANX, NetUtils.getLocalAddress().getHostAddress(), 0, RegistryService.class.getName());
        final Map<String, String> attachments = new HashMap<String, String>();
        attachments.put(URLParamType.host.getName(), URLParamType.host.getValue());
        attachments.put(URLParamType.application.getName(), URLParamType.application.getValue());
        attachments.put(URLParamType.module.getName(), URLParamType.module.getValue());
        // TODO 优化重复代码
        mockery.checking(new Expectations() {
            {
                oneOf(caller).call(request);
                will(returnValue(response));
                atLeast(2).of(caller).getUrl();
                will(returnValue(url));
                oneOf(response).getException();
                will(returnValue(null));
                oneOf(response).getProcessTime();
                will(returnValue(1000L));
                allowing(request).getInterfaceName();
                will(returnValue(IHello.class.getName()));
                allowing(request).getParamtersDesc();
                will(returnValue("mock_param_desc"));
                allowing(request).getMethodName();
                will(returnValue("mock_method_name"));
                atLeast(1).of(request).getAttachments();
                will(returnValue(attachments));
            }
        });

        assertEquals(response, accessStatisticFilter.filter(caller, request));

        mockery.checking(new Expectations() {
            {
                oneOf(caller).call(request);
                will(returnValue(null));
                oneOf(caller).getUrl();
                will(returnValue(url));
                allowing(request).getInterfaceName();
                will(returnValue(IHello.class.getName()));
                allowing(request).getParamtersDesc();
                will(returnValue("mock_param_desc"));
                allowing(request).getMethodName();
                will(returnValue("mock_method_name"));

            }
        });

        assertNull(accessStatisticFilter.filter(caller, request));

        mockery.checking(new Expectations() {
            {
                oneOf(caller).call(request);
                will(returnValue(response));
                oneOf(caller).getUrl();
                will(returnValue(url));
                atMost(2).of(response).getException();
                will(returnValue(new MotanxBizException()));
                oneOf(response).getProcessTime();
                will(returnValue(1000L));
                allowing(request).getInterfaceName();
                will(returnValue(IHello.class.getName()));
                allowing(request).getParamtersDesc();
                will(returnValue("mock_param_desc"));
                allowing(request).getMethodName();
                will(returnValue("mock_method_name"));

            }
        });

        assertEquals(response, accessStatisticFilter.filter(caller, request));

        mockery.checking(new Expectations() {
            {
                oneOf(caller).call(request);
                will(returnValue(response));
                oneOf(caller).getUrl();
                will(returnValue(url));
                atMost(2).of(response).getException();
                will(returnValue(new MotanxServiceException()));
                oneOf(response).getProcessTime();
                will(returnValue(1000L));
                allowing(request).getInterfaceName();
                will(returnValue(IHello.class.getName()));
                allowing(request).getParamtersDesc();
                will(returnValue("mock_param_desc"));
                allowing(request).getMethodName();
                will(returnValue("mock_method_name"));

            }
        });

        assertEquals(response, accessStatisticFilter.filter(caller, request));
    }
}
