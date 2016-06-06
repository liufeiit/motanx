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

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.Activation;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.Caller;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.RpcStats;

/**
 * 
 * limit active count，判断某个接口并发数是否超限，如果超过限制，则上抛异常,同时做简单的统计。 此filter比较严格，尽量放到底层较早执行。
 * 
 * @author fishermen
 * @version V1.0 created at: 2013-5-23
 */
@SpiMeta(name = "active")
@Activation(sequence = 1)
public class ActiveLimitFilter implements Filter {

    @Override
    public Response filter(Caller<?> caller, Request request) {
        int maxAcvitivyCount = caller.getUrl().getIntParameter(URLParamType.actives.getName(), URLParamType.actives.getIntValue());
        if (maxAcvitivyCount > 0) {
            int activeCount = RpcStats.getServiceStat(caller.getUrl()).getActiveCount();
            if (activeCount >= maxAcvitivyCount) {
                throw new MotanxServiceException(String.format("Request(%s) active count exceed the limit (%s), referer:%s", request,
                        maxAcvitivyCount, caller.getUrl()), MotanxErrorMsgConstant.SERVICE_REJECT);
            }
        }

        long startTime = System.currentTimeMillis();
        RpcStats.beforeCall(caller.getUrl(), request);
        try {
            Response rs = caller.call(request);
            RpcStats.afterCall(caller.getUrl(), request, true, System.currentTimeMillis() - startTime);
            return rs;
        } catch (RuntimeException re) {
            RpcStats.afterCall(caller.getUrl(), request, false, System.currentTimeMillis() - startTime);
            throw re;
        }

    }

}
