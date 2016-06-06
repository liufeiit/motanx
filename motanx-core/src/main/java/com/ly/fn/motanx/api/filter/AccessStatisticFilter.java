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

import com.ly.fn.motanx.api.common.MotanxConstants;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.rpc.*;
import com.ly.fn.motanx.api.util.ExceptionUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;
import com.ly.fn.motanx.api.util.StatsUtil;
import com.ly.fn.motanx.api.util.StatsUtil.AccessStatus;

/**
 * @author maijunsheng
 * @version 创建时间：2013-6-14
 */
@SpiMeta(name = "statistic")
public class AccessStatisticFilter implements Filter {
    @Override
    public Response filter(Caller<?> caller, Request request) {
        long start = System.currentTimeMillis();
        AccessStatus accessStatus = AccessStatus.NORMAL;
        boolean specialException = true;
        long bizProcessTime = 0;

        try {
            Response response = caller.call(request);

            if (response == null) {
                accessStatus = AccessStatus.OTHER_EXCEPTION;
            } else {
                if (response.getException() != null) {
                    if (ExceptionUtil.isBizException(response.getException())) {
                        accessStatus = AccessStatus.BIZ_EXCEPTION;
                    } else {
                        accessStatus = AccessStatus.OTHER_EXCEPTION;
                    }
                }

                specialException = false;
                bizProcessTime = response.getProcessTime();
            }

            return response;
        } finally {
            long end = System.currentTimeMillis();

            if (specialException) {
                accessStatus = AccessStatus.OTHER_EXCEPTION;
                bizProcessTime = end - start;
            }

            Application application;
            if (caller instanceof Provider) {
                application = new Application(ApplicationInfo.STATISTIC, "rpc_service");
            } else {
                application = ApplicationInfo.getApplication(caller.getUrl());
            }
            StatsUtil.accessStatistic(
                    caller.getUrl().getProtocol() + MotanxConstants.PROTOCOL_SEPARATOR + MotanxFrameworkUtil.getFullMethodString(request),
                    application, end, end - start, bizProcessTime, accessStatus);

        }
    }
}
