package com.ly.fn.motanx.api.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ly.fn.motanx.api.common.MotanxConstants;

/**
 * 摘要算法辅助类
 */
public class MotanxDigestUtil {

    private static Logger log = LoggerFactory.getLogger(MotanxDigestUtil.class);

    private static ThreadLocal<CRC32> crc32Provider = new ThreadLocal<CRC32>() {
        @Override
        protected CRC32 initialValue() {
            return new CRC32();
        }
    };

    public static long getCrc32(String str) {
        try {
            return getCrc32(str.getBytes(MotanxConstants.DEFAULT_CHARACTER));
        } catch (UnsupportedEncodingException e) {
            log.warn(String.format("Error: getCrc32, str=%s", str), e);
            return -1;
        }
    }

    public static long getCrc32(byte[] b) {
        CRC32 crc = crc32Provider.get();
        crc.reset();
        crc.update(b);
        return crc.getValue();
    }

    /*
     * 全小写32位MD5
     */
    public static String md5LowerCase(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte element : b) {
                i = element;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("md5 digest error!", e);
        }
        return null;
    }
}
