package com.ly.fn.motanx.api.protocol.injvm;

import com.ly.fn.motanx.api.core.extension.SpiMeta;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.protocol.AbstractProtocol;
import com.ly.fn.motanx.api.rpc.AbstractExporter;
import com.ly.fn.motanx.api.rpc.AbstractReferer;
import com.ly.fn.motanx.api.rpc.Exporter;
import com.ly.fn.motanx.api.rpc.Provider;
import com.ly.fn.motanx.api.rpc.Referer;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.rpc.URL;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.MotanxFrameworkUtil;

/**
 * JVM 节点内部的调用
 * 
 * <pre>
 * 		1) provider 和 referer 相对应 
 * 		2) provider 需要在被consumer refer 之前需要 export
 * </pre>
 */
@SpiMeta(name = "injvm")
public class InjvmProtocol extends AbstractProtocol {

    @Override
    protected <T> Exporter<T> createExporter(Provider<T> provider, URL url) {
        return new InJvmExporter<T>(provider, url);
    }

    @Override
    protected <T> Referer<T> createReferer(Class<T> clz, URL url, URL serviceUrl) {
        return new InjvmReferer<T>(clz, url, serviceUrl);
    }

    /**
     * injvm provider
     */
    class InJvmExporter<T> extends AbstractExporter<T> {
        public InJvmExporter(Provider<T> provider, URL url) {
            super(provider, url);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void unexport() {
            String protocolKey = MotanxFrameworkUtil.getProtocolKey(url);

            Exporter<T> exporter = (Exporter<T>) exporterMap.remove(protocolKey);

            if (exporter != null) {
                exporter.destroy();
            }

            LoggerUtil.info("InJvmExporter unexport Success: url=" + url);
        }

        @Override
        protected boolean doInit() {
            return true;
        }

        @Override
        public void destroy() {}
    }

    /**
     * injvm consumer
     */
    class InjvmReferer<T> extends AbstractReferer<T> {
        private Exporter<T> exporter;

        public InjvmReferer(Class<T> clz, URL url, URL serviceUrl) {
            super(clz, url, serviceUrl);
        }

        @Override
        protected Response doCall(Request request) {
            if (exporter == null) {
                throw new MotanxServiceException("InjvmReferer call Error: provider not exist, url=" + url.getUri(), MotanxErrorMsgConstant.SERVICE_UNFOUND);
            }

            return exporter.getProvider().call(request);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected boolean doInit() {
            String protocolKey = MotanxFrameworkUtil.getProtocolKey(url);

            exporter = (Exporter<T>) exporterMap.get(protocolKey);

            if (exporter == null) {
                LoggerUtil.error("InjvmReferer init Error: provider not exist, url=" + url);
                return false;
            }

            return true;
        }

        @Override
        public void destroy() {}
    }
}
