package com.ly.fn.motanx.api.transport;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.ly.fn.motanx.api.exception.MotanxBizException;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.protocol.rpc.CompressRpcCodec;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Provider;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;
import com.ly.fn.motanx.api.util.ReflectUtil;

/**
 * service 消息处理
 * 
 * <pre>
 * 		1） 多个service的支持
 * 		2） 区分service的方式： group/interface/version
 * </pre>
 */
public class ProviderMessageRouter implements MessageHandler {
    private Map<String, Provider<?>> providers = new HashMap<String, Provider<?>>();

    // 所有暴露出去的方法计数
    // 比如：messageRouter 里面涉及2个Service: ServiceA 有5个public method，ServiceB
    // 有10个public method，那么就是15
    protected AtomicInteger methodCounter = new AtomicInteger(0);

    public ProviderMessageRouter() {}

    public ProviderMessageRouter(Provider<?> provider) {
        addProvider(provider);
    }

    @Override
    public Object handle(Channel channel, Object message) {
        if (channel == null || message == null) {
            throw new MotanxFrameworkException("RequestRouter handler(channel, message) params is null");
        }

        if (!(message instanceof Request)) {
            throw new MotanxFrameworkException("RequestRouter message type not support: " + message.getClass());
        }

        Request request = (Request) message;

        String serviceKey = MotanxFrameworkUtil.getServiceKey(request);

        Provider<?> provider = providers.get(serviceKey);

        if (provider == null) {
            LoggerUtil.error(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey + " " + MotanxFrameworkUtil.toString(request));
            MotanxServiceException exception =
                    new MotanxServiceException(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey + " " + MotanxFrameworkUtil.toString(request));

            DefaultResponse response = new DefaultResponse();
            response.setException(exception);
            return response;
        }

        return call(request, provider);
    }

    protected Response call(Request request, Provider<?> provider) {
        try {
            return provider.call(request);
        } catch (Exception e) {
            DefaultResponse response = new DefaultResponse();
            response.setException(new MotanxBizException("provider call process error", e));
            return response;
        }
    }

    public synchronized void addProvider(Provider<?> provider) {
        String serviceKey = MotanxFrameworkUtil.getServiceKey(provider.getUrl());
        if (providers.containsKey(serviceKey)) {
            throw new MotanxFrameworkException("provider alread exist: " + serviceKey);
        }

        providers.put(serviceKey, provider);

        // 获取该service暴露的方法数：
        List<Method> methods = ReflectUtil.getPublicMethod(provider.getInterface());
        CompressRpcCodec.putMethodSign(provider, methods);// 对所有接口方法生成方法签名。适配方法签名压缩调用方式。

        int publicMethodCount = methods.size();
        methodCounter.addAndGet(publicMethodCount);

        LoggerUtil.info("RequestRouter addProvider: url=" + provider.getUrl() + " all_public_method_count=" + methodCounter.get());
    }

    public synchronized void removeProvider(Provider<?> provider) {
        String serviceKey = MotanxFrameworkUtil.getServiceKey(provider.getUrl());

        providers.remove(serviceKey);
        List<Method> methods = ReflectUtil.getPublicMethod(provider.getInterface());
        int publicMethodCount = methods.size();
        methodCounter.getAndSet(methodCounter.get() - publicMethodCount);

        LoggerUtil.info("RequestRouter removeProvider: url=" + provider.getUrl() + " all_public_method_count=" + methodCounter.get());
    }

    public int getPublicMethodCount() {
        return methodCounter.get();
    }
}
