package com.ly.fn.motanx.api.transport.netty;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.execution.ExecutionHandler;

import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.util.LoggerUtil;

public class ProtectedExecutionHandler extends ExecutionHandler {
    private ThreadPoolExecutor threadPoolExecutor;

    ProtectedExecutionHandler(final ThreadPoolExecutor threadPoolExecutor) {
        super(threadPoolExecutor);
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * if RejectedExecutionException happen, send 503 exception to client
     */
    @Override
    public void handleUpstream(ChannelHandlerContext context, ChannelEvent e) throws Exception {
        try {
            super.handleUpstream(context, e);
        } catch (RejectedExecutionException rejectException) {
            if (e instanceof MessageEvent) {
                if (((MessageEvent) e).getMessage() instanceof Request) {
                    Request request = (Request) ((MessageEvent) e).getMessage();
                    DefaultResponse response = new DefaultResponse();
                    response.setRequestId(request.getRequestId());
                    response.setException(new MotanxServiceException("process thread pool is full, reject", MotanxErrorMsgConstant.SERVICE_REJECT));
                    e.getChannel().write(response);

                    LoggerUtil.debug("process thread pool is full, reject, active={} poolSize={} corePoolSize={} maxPoolSize={} taskCount={} requestId={}", threadPoolExecutor.getActiveCount(),
                            threadPoolExecutor.getPoolSize(), threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getMaximumPoolSize(), threadPoolExecutor.getTaskCount(), request.getRequestId());
                }
            }
        }
    }

}
