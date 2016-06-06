package com.ly.fn.motanx.api.util;

public class MathUtil {

    public static int parseInt(String intStr, int defaultValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            LoggerUtil.debug("ParseInt false, for malformed intStr:" + intStr);
            return defaultValue;
        }
    }
}
