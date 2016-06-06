package com.ly.fn.motanx.api.protocol.rpc;

import java.util.HashMap;
import java.util.Map;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.core.extension.ExtensionLoader;
import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.protocol.AbstractProtocol;
import com.ly.fn.motanx.api.rpc.AbstractExporter;
import com.ly.fn.motanx.api.rpc.AbstractReferer;
import com.ly.fn.motanx.api.rpc.Exporter;
import com.ly.fn.motanx.api.rpc.Future;
import com.ly.fn.motanx.api.rpc.FutureListener;
import com.ly.fn.motanx.api.rpc.Provider;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.transport.Client;
import com.ly.fn.motanx.api.transport.EndpointFactory;
import com.ly.fn.motanx.api.transport.ProviderMessageRouter;
import com.ly.fn.motanx.api.transport.ProviderProtectedMessageRouter;
import com.ly.fn.motanx.api.transport.Server;
import com.ly.fn.motanx.api.transport.TransportException;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

@SpiMeta(name = "motanx")
public class DefaultRpcProtocol extends AbstractProtocol {

    // 多个service可能在相同端口进行服务暴露，因此来自同个端口的请求需要进行路由以找到相应的服务，同时不在该端口暴露的服务不应该被找到
    private Map<String, ProviderMessageRouter> ipPort2RequestRouter = new HashMap<String, ProviderMessageRouter>();

    @Override
    protected <T> Exporter<T> createExporter(Provider<T> provider, URL url) {
        return new DefaultRpcExporter<T>(provider, url);
    }

    @Override
    protected <T> Referer<T> createReferer(Class<T> clz, URL url, URL serviceUrl) {
        return new DefaultRpcReferer<T>(clz, url, serviceUrl);
    }

    /**
     * rpc provider
     */
    class DefaultRpcExporter<T> extends AbstractExporter<T> {
        private Server server;
        private EndpointFactory endpointFactory;

        public DefaultRpcExporter(Provider<T> provider, URL url) {
            super(provider, url);
            ProviderMessageRouter requestRouter = initRequestRouter(url);
            String endpointFactoryName = url.getParameter(URLParamType.endpointFactory.getName(), URLParamType.endpointFactory.getValue());
            endpointFactory = ExtensionLoader.getExtensionLoader(EndpointFactory.class).getExtension(endpointFactoryName);
            server = endpointFactory.createServer(url, requestRouter);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void unexport() {
            String protocolKey = MotanxFrameworkUtil.getProtocolKey(url);
            String ipPort = url.getServerPortStr();

            Exporter<T> exporter = (Exporter<T>) exporterMap.remove(protocolKey);

            if (exporter != null) {
                exporter.destroy();
            }

            synchronized (ipPort2RequestRouter) {
                ProviderMessageRouter requestRouter = ipPort2RequestRouter.get(ipPort);

                if (requestRouter != null) {
                    requestRouter.removeProvider(provider);
                }
            }

            LoggerUtil.info("DefaultRpcExporter unexport Success: url={}", url);
        }

        @Override
        protected boolean doInit() {
            boolean result = server.open();

            return result;
        }

        @Override
        public boolean isAvailable() {
            return server.isAvailable();
        }

        @Override
        public void destroy() {
            endpointFactory.safeReleaseResource(server, url);
            LoggerUtil.info("DefaultRpcExporter destory Success: url={}", url);
        }

        private ProviderMessageRouter initRequestRouter(URL url) {
            ProviderMessageRouter requestRouter = null;
            String ipPort = url.getServerPortStr();

            synchronized (ipPort2RequestRouter) {
                requestRouter = ipPort2RequestRouter.get(ipPort);

                if (requestRouter == null) {
                    requestRouter = new ProviderProtectedMessageRouter(provider);
                    ipPort2RequestRouter.put(ipPort, requestRouter);
                } else {
                    requestRouter.addProvider(provider);
                }
            }

            return requestRouter;
        }
    }

    /**
     * rpc referer
     *
     * @param <T>
     * @author maijunsheng
     */
    class DefaultRpcReferer<T> extends AbstractReferer<T> {
        private Client client;
        private EndpointFactory endpointFactory;

        public DefaultRpcReferer(Class<T> clz, URL url, URL serviceUrl) {
            super(clz, url, serviceUrl);

            endpointFactory = ExtensionLoader.getExtensionLoader(EndpointFactory.class).getExtension(url.getParameter(URLParamType.endpointFactory.getName(), URLParamType.endpointFactory.getValue()));

            client = endpointFactory.createClient(url);
        }

        @Override
        protected Response doCall(Request request) {
            try {
                // 为了能够实现跨group请求，需要使用server端的group。
                request.setAttachment(URLParamType.group.getName(), serviceUrl.getGroup());
                return client.request(request);
            } catch (TransportException exception) {
                throw new MotanxServiceException("DefaultRpcReferer call Error: url=" + url.getUri(), exception);
            }
        }

        @Override
        protected void decrActiveCount(Request request, Response response) {
            if (response == null || !(response instanceof Future)) {
                activeRefererCount.decrementAndGet();
                return;
            }

            Future future = (Future) response;

            future.addListener(new FutureListener() {
                @Override
                public void operationComplete(Future future) throws Exception {
                    activeRefererCount.decrementAndGet();
                }
            });
        }

        @Override
        protected boolean doInit() {
            boolean result = client.open();

            return result;
        }

        @Override
        public boolean isAvailable() {
            return client.isAvailable();
        }

        @Override
        public void destroy() {
            endpointFactory.safeReleaseResource(client, url);
            LoggerUtil.info("DefaultRpcReferer destory client: url={}" + url);
        }
    }
}
