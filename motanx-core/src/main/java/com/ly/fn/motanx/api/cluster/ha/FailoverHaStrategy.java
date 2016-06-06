package com.ly.fn.motanx.api.cluster.ha;

import com.ly.fn.motanx.api.cluster.LoadBalance;
import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.ExceptionUtil;
import com.ly.fn.motanx.api.util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;

@SpiMeta(name = "failover")
public class FailoverHaStrategy<T> extends AbstractHaStrategy<T> {

    protected ThreadLocal<List<Referer<T>>> referersHolder = new ThreadLocal<List<Referer<T>>>() {
        @Override
        protected java.util.List<com.ly.fn.motanx.api.rpc.Referer<T>> initialValue() {
            return new ArrayList<Referer<T>>();
        }
    };

    @Override
    public Response call(Request request, LoadBalance<T> loadBalance) {

        List<Referer<T>> referers = selectReferers(request, loadBalance);
        if (referers.isEmpty()) {
            throw new MotanxServiceException(String.format("FailoverHaStrategy No referers for request:%s, loadbalance:%s", request, loadBalance));
        }
        URL refUrl = referers.get(0).getUrl();
        // 先使用method的配置
        int tryCount = refUrl.getMethodParameter(request.getMethodName(), request.getParamtersDesc(), URLParamType.retries.getName(), URLParamType.retries.getIntValue());
        // 如果有问题，则设置为不重试
        if (tryCount < 0) {
            tryCount = 0;
        }

        for (int i = 0; i <= tryCount; i++) {
            Referer<T> refer = referers.get(i % referers.size());
            try {
                request.setRetries(i);
                return refer.call(request);
            } catch (RuntimeException e) {
                // 对于业务异常，直接抛出
                if (ExceptionUtil.isBizException(e)) {
                    throw e;
                } else if (i >= tryCount) {
                    throw e;
                }
                LoggerUtil.warn(String.format("FailoverHaStrategy Call false for request:%s error=%s", request, e.getMessage()));
            }
        }

        throw new MotanxFrameworkException("FailoverHaStrategy.call should not come here!");
    }

    protected List<Referer<T>> selectReferers(Request request, LoadBalance<T> loadBalance) {
        List<Referer<T>> referers = referersHolder.get();
        referers.clear();
        loadBalance.selectToHolder(request, referers);
        return referers;
    }

}
