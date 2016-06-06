package com.ly.fn.motanx.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.ly.fn.motanx.api.common.MotanxConstants;

public class StringTools {

    public static int parseInteger(String intStr) {
        if (intStr == null) {
            return MotanxConstants.DEFAULT_INT_VALUE;
        }
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            return MotanxConstants.DEFAULT_INT_VALUE;
        }
    }

    public static String urlEncode(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        try {
            return URLEncoder.encode(value, MotanxConstants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String urlDecode(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        try {
            return URLDecoder.decode(value, MotanxConstants.DEFAULT_CHARACTER);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String toQueryString(Map<String, String> ps) {
        StringBuilder buf = new StringBuilder();
        if (ps != null && ps.size() > 0) {
            for (Map.Entry<String, String> entry : new TreeMap<String, String>(ps).entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && key.length() > 0 && value != null && value.length() > 0) {
                    if (buf.length() > 0) {
                        buf.append("&");
                    }
                    buf.append(key);
                    buf.append("=");
                    buf.append(value);
                }
            }
        }
        return buf.toString();
    }
}
