package com.ly.fn.motanx.api.rpc;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ly.fn.motanx.api.util.LoggerUtil;

public class RefererSupports {

    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);

    // 正常情况下请求超过1s已经是能够忍耐的极限值了，delay 1s进行destroy
    private static final int DELAY_TIME = 1000;

    public static <T> void delayDestroy(final List<Referer<T>> referers) {
        if (referers == null || referers.isEmpty()) {
            return;
        }

        scheduledExecutor.schedule(new Runnable() {
            @Override
            public void run() {

                for (Referer<?> referer : referers) {
                    try {
                        referer.destroy();
                    } catch (Exception e) {
                        LoggerUtil.error("RefererSupports delayDestroy Error: url=" + referer.getUrl().getUri(), e);
                    }
                }
            }
        }, DELAY_TIME, TimeUnit.MILLISECONDS);

        LoggerUtil.info("RefererSupports delayDestroy Success: size={} service={} urls={}", referers.size(), referers.get(0).getUrl()
                .getIdentity(), getServerPorts(referers));
    }

    private static <T> String getServerPorts(List<Referer<T>> referers) {
        if (referers == null || referers.isEmpty()) {
            return "[]";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (Referer<T> referer : referers) {
            builder.append(referer.getUrl().getServerPortStr()).append(",");
        }
        builder.setLength(builder.length() - 1);
        builder.append("]");

        return builder.toString();
    }
}
