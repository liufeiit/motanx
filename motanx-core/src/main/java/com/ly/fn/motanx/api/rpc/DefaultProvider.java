package com.ly.fn.motanx.api.rpc;

import java.lang.reflect.Method;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxBizException;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.util.LoggerUtil;

@SpiMeta(name = "motanx")
public class DefaultProvider<T> extends AbstractProvider<T> {
    protected T proxyImpl;

    public DefaultProvider(T proxyImpl, URL url, Class<T> clz) {
        super(url, clz);
        this.proxyImpl = proxyImpl;
    }

    @Override
    public Response invoke(Request request) {
        DefaultResponse response = new DefaultResponse();
        Method method = lookup(request);
        if (method == null) {
            MotanxServiceException exception = new MotanxServiceException(
                    "Service method not exist: " + request.getInterfaceName() + "." + request.getMethodName() + "(" + request.getParamtersDesc() + ")", MotanxErrorMsgConstant.SERVICE_UNFOUND);
            response.setException(exception);
            return response;
        }
        try {
            Object value = method.invoke(proxyImpl, request.getArguments());
            response.setValue(value);
        } catch (Exception e) {
            if (e.getCause() != null) {
                LoggerUtil.error("Exception caught when method invoke: " + e.getCause());
                response.setException(new MotanxBizException("provider call process error", e.getCause()));
            } else {
                response.setException(new MotanxBizException("provider call process error", e));
            }
        } catch (Throwable t) {
            // 如果服务发生Error，将Error转化为Exception，防止拖垮调用方
            if (t.getCause() != null) {
                response.setException(new MotanxServiceException("provider has encountered a fatal error!", t.getCause()));
            } else {
                response.setException(new MotanxServiceException("provider has encountered a fatal error!", t));
            }

        }
        // 传递rpc版本和attachment信息方便不同rpc版本的codec使用。
        response.setRpcProtocolVersion(request.getRpcProtocolVersion());
        response.setAttachments(request.getAttachments());
        return response;
    }

}
