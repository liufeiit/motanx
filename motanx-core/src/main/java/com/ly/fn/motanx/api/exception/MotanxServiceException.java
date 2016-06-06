package com.ly.fn.motanx.api.exception;

public class MotanxServiceException extends MotanxAbstractException {
    private static final long serialVersionUID = -3491276058323309898L;

    public MotanxServiceException() {
        super(MotanxErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public MotanxServiceException(MotanxErrorMsg motanErrorMsg) {
        super(motanErrorMsg);
    }

    public MotanxServiceException(String message) {
        super(message, MotanxErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public MotanxServiceException(String message, MotanxErrorMsg motanErrorMsg) {
        super(message, motanErrorMsg);
    }

    public MotanxServiceException(String message, Throwable cause) {
        super(message, cause, MotanxErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public MotanxServiceException(String message, Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(message, cause, motanErrorMsg);
    }

    public MotanxServiceException(Throwable cause) {
        super(cause, MotanxErrorMsgConstant.SERVICE_DEFAULT_ERROR);
    }

    public MotanxServiceException(Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(cause, motanErrorMsg);
    }
}
