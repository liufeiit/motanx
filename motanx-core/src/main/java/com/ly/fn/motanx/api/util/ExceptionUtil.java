package com.ly.fn.motanx.api.util;

import com.ly.fn.motanx.api.exception.MotanxAbstractException;
import com.ly.fn.motanx.api.exception.MotanxBizException;

public class ExceptionUtil {

    /**
     * 判定是否是业务方的逻辑抛出的异常
     * 
     * <pre>
     * 		true: 来自业务方的异常
     * 		false: 来自框架本身的异常
     * </pre>
     * 
     * @param e
     * @return
     */
    public static boolean isBizException(Exception e) {
        return e instanceof MotanxBizException;
    }


    /**
     * 是否框架包装过的异常
     * 
     * @param e
     * @return
     */
    public static boolean isMotanException(Exception e) {
        return e instanceof MotanxAbstractException;
    }
}
