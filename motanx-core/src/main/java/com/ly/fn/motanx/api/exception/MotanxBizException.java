package com.ly.fn.motanx.api.exception;

public class MotanxBizException extends MotanxAbstractException {
    private static final long serialVersionUID = -3491276058323309898L;

    public MotanxBizException() {
        super(MotanxErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public MotanxBizException(MotanxErrorMsg motanErrorMsg) {
        super(motanErrorMsg);
    }

    public MotanxBizException(String message) {
        super(message, MotanxErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public MotanxBizException(String message, MotanxErrorMsg motanErrorMsg) {
        super(message, motanErrorMsg);
    }

    public MotanxBizException(String message, Throwable cause) {
        super(message, cause, MotanxErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public MotanxBizException(String message, Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(message, cause, motanErrorMsg);
    }

    public MotanxBizException(Throwable cause) {
        super(cause, MotanxErrorMsgConstant.BIZ_DEFAULT_EXCEPTION);
    }

    public MotanxBizException(Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(cause, motanErrorMsg);
    }
}
