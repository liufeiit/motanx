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
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.filter.ActiveLimitFilter;
import com.ly.fn.motanx.api.protocol.example.IHello;
import com.ly.fn.motanx.api.registry.RegistryService;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.RpcStats;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.NetUtils;

/**
 * 
 * 类说明
 * 
 * @author fishermen
 * @version V1.0 created at: 2013-6-28
 */

public class ActiveLimitFilterTest extends BaseTestCase {

    private ActiveLimitFilter activeLimitFilter = new ActiveLimitFilter();

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testFilter() {
        final Request request = mockery.mock(Request.class);
        final Response response = mockery.mock(Response.class);
        final Caller<IHello> caller = mockery.mock(Caller.class);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(URLParamType.actives.getName(), "" + 3);
        final URL url =
                new URL(MotanxConstants.PROTOCOL_MOTANX, NetUtils.getLocalAddress().getHostAddress(), 0, RegistryService.class.getName(),
                        parameters);

        mockery.checking(new Expectations() {
            {
                oneOf(caller).call(request);
                will(returnValue(response));
                atLeast(1).of(caller).getUrl();
                will(returnValue(url));
                atLeast(1).of(request).getMethodName();
                will(returnValue("mock_mothod_name"));
                atLeast(1).of(request).getParamtersDesc();
                will(returnValue("mock_param_desc"));

            }
        });

        activeLimitFilter.filter(caller, request);

        for (int i = 0; i < 4; i++) {
            RpcStats.beforeCall(url, request);
        }
        try {
            activeLimitFilter.filter(caller, request);
            assertFalse(true);
        } catch (MotanxServiceException e) {
            assertEquals(MotanxErrorMsgConstant.SERVICE_REJECT, e.getMotanErrorMsg());
        } catch (Exception e) {
            assertFalse(true);
        }
    }
}
