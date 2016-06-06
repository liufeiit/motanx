package com.ly.fn.motanx.api.config.springsupport;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.ly.fn.motanx.api.config.ServiceConfig;
import com.ly.fn.motanx.api.util.LoggerUtil;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2016年5月18日 下午9:09:00
 */
public class BasicRpcServiceExporter implements InitializingBean, DisposableBean {

    private final static int DEFAULT_COREPOOLSIZE = 1;

    private static final String RPC_SERVICE_EXPORTER_WORKER = "RPC-Service-Exporter-Worker-";
    
    protected boolean async = false;

    protected int corePoolSize = DEFAULT_COREPOOLSIZE;

    protected ThreadPoolExecutor executor;
    
    protected final ConcurrentHashMap<String, ServiceConfig<?>> exportedServices = new ConcurrentHashMap<String, ServiceConfig<?>>();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (async) {
            RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
            ThreadFactory threadFactory = new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    final UncaughtExceptionHandler eh = Thread.getDefaultUncaughtExceptionHandler();
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r);
                    String threadName = RPC_SERVICE_EXPORTER_WORKER + System.currentTimeMillis();
                    LoggerUtil.info("ThreadFactory : newThread named {}", threadName);
                    t.setName(threadName);
                    if (t.getPriority() != Thread.NORM_PRIORITY) {
                        t.setPriority(Thread.NORM_PRIORITY);
                    }
                    t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                        public void uncaughtException(Thread t, Throwable e) {
                            LoggerUtil.info("RpcServiceExporter Worker UncaughtExceptionHandler " + t.getName() + " Error.", e);
                            eh.uncaughtException(t, e);
                        }
                    });
                    return t;
                }
            };
            executor = new ThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory, rejectedExecutionHandler);
        }
    }

    @Override
    public final void destroy() throws Exception {
        if (exportedServices.isEmpty()) {
            close();
            return;
        }
        for (final ServiceConfig<?> serviceConfig : exportedServices.values()) {
            if (async) {
                executor.submit(new Runnable() {
                    public void run() {
                        serviceConfig.unexport();
                    }
                });
                continue;
            }
            serviceConfig.unexport();
        }
        close();
    }
    
    protected void init() throws Exception {}
    
    protected void close() throws Exception {}
}
