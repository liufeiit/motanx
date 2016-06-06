package com.ly.fn.motanx.api.transport.netty;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import com.ly.fn.motanx.api.common.URLParamType;
import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;
import com.ly.fn.motanx.api.exception.MotanxServiceException;
import com.ly.fn.motanx.api.rpc.DefaultResponse;
import com.ly.fn.motanx.api.rpc.Request;
import com.ly.fn.motanx.api.rpc.Response;
import com.ly.fn.motanx.api.transport.Channel;
import com.ly.fn.motanx.api.transport.MessageHandler;
import com.ly.fn.motanx.api.util.LoggerUtil;
import com.ly.fn.motanx.api.util.NetUtils;

public class NettyChannelHandler extends SimpleChannelHandler {
    private ThreadPoolExecutor threadPoolExecutor;
    private MessageHandler messageHandler;
    private Channel serverChannel;

    public NettyChannelHandler(Channel serverChannel) {
        this.serverChannel = serverChannel;
    }

    public NettyChannelHandler(Channel serverChannel, MessageHandler messageHandler) {
        this.serverChannel = serverChannel;
        this.messageHandler = messageHandler;
    }

    public NettyChannelHandler(Channel serverChannel, MessageHandler messageHandler, ThreadPoolExecutor threadPoolExecutor) {
        this.serverChannel = serverChannel;
        this.messageHandler = messageHandler;
        this.threadPoolExecutor = threadPoolExecutor;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LoggerUtil.info(
                "NettyChannelHandler channelConnected: remote=" + ctx.getChannel().getRemoteAddress() + " local=" + ctx.getChannel().getLocalAddress() + " event=" + e.getClass().getSimpleName());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        LoggerUtil.info(
                "NettyChannelHandler channelDisconnected: remote=" + ctx.getChannel().getRemoteAddress() + " local=" + ctx.getChannel().getLocalAddress() + " event=" + e.getClass().getSimpleName());
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Object message = e.getMessage();

        if (message instanceof Request) {
            processRequest(ctx, e);
        } else if (message instanceof Response) {
            processResponse(ctx, e);
        } else {
            LoggerUtil.error("NettyChannelHandler messageReceived type not support: class=" + message.getClass());
            throw new MotanxFrameworkException("NettyChannelHandler messageReceived type not support: class=" + message.getClass());
        }
    }

    /**
     * <pre>
     *  request process: 主要来自于client的请求，需要使用threadPoolExecutor进行处理，避免service message处理比较慢导致iothread被阻塞
     * </pre>
     * 
     * @param ctx
     * @param e
     */
    private void processRequest(final ChannelHandlerContext ctx, MessageEvent e) {
        final Request request = (Request) e.getMessage();
        request.setAttachment(URLParamType.host.getName(), NetUtils.getHostName(ctx.getChannel().getRemoteAddress()));

        final long processStartTime = System.currentTimeMillis();

        // 使用线程池方式处理
        try {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO 线程上下文初始化（requestIdFromClient）
                    long requestId = 0;
                    try {
                        if (request.getAttachments() == null || !request.getAttachments().containsKey(URLParamType.requestIdFromClient.getName())) {
                            requestId = 0;
                        } else {
                            requestId = Long.valueOf(request.getAttachments().get(URLParamType.requestIdFromClient.getName()));
                        }
                    } catch (Exception e) {
                        LoggerUtil.error("Transfer request id error!", e.getCause());
                    }
                    // TODO 上下文
                    // RequestTraceContext.init(requestId);
                    processRequest(ctx, request, processStartTime);
                    // RequestTraceContext.finish();
                }
            });
        } catch (RejectedExecutionException rejectException) {
            DefaultResponse response = new DefaultResponse();
            response.setRequestId(request.getRequestId());
            response.setException(new MotanxServiceException("process thread pool is full, reject", MotanxErrorMsgConstant.SERVICE_REJECT));
            response.setProcessTime(System.currentTimeMillis() - processStartTime);
            e.getChannel().write(response);

            LoggerUtil.debug("process thread pool is full, reject, active={} poolSize={} corePoolSize={} maxPoolSize={} taskCount={} requestId={}", threadPoolExecutor.getActiveCount(),
                    threadPoolExecutor.getPoolSize(), threadPoolExecutor.getCorePoolSize(), threadPoolExecutor.getMaximumPoolSize(), threadPoolExecutor.getTaskCount(), request.getRequestId());
        }
    }

    private void processRequest(ChannelHandlerContext ctx, Request request, long processStartTime) {
        Object result = messageHandler.handle(serverChannel, request);

        DefaultResponse response = null;

        if (!(result instanceof DefaultResponse)) {
            response = new DefaultResponse(result);
        } else {
            response = (DefaultResponse) result;
        }

        response.setRequestId(request.getRequestId());
        response.setProcessTime(System.currentTimeMillis() - processStartTime);

        if (ctx.getChannel().isConnected()) {
            ctx.getChannel().write(response);
        }
    }

    private void processResponse(ChannelHandlerContext ctx, MessageEvent e) {
        messageHandler.handle(serverChannel, e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        LoggerUtil.error("NettyChannelHandler exceptionCaught: remote=" + ctx.getChannel().getRemoteAddress() + " local=" + ctx.getChannel().getLocalAddress() + " event=" + e.getCause(),
                e.getCause());

        ctx.getChannel().close();
    }
}
