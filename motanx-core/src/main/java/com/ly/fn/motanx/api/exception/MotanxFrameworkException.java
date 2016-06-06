package com.ly.fn.motanx.api.exception;

public class MotanxFrameworkException extends MotanxAbstractException {
    private static final long serialVersionUID = -1638857395789735293L;

    public MotanxFrameworkException() {
        super(MotanxErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public MotanxFrameworkException(MotanxErrorMsg motanErrorMsg) {
        super(motanErrorMsg);
    }

    public MotanxFrameworkException(String message) {
        super(message, MotanxErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public MotanxFrameworkException(String message, MotanxErrorMsg motanErrorMsg) {
        super(message, motanErrorMsg);
    }

    public MotanxFrameworkException(String message, Throwable cause) {
        super(message, cause, MotanxErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public MotanxFrameworkException(String message, Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(message, cause, motanErrorMsg);
    }

    public MotanxFrameworkException(Throwable cause) {
        super(cause, MotanxErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR);
    }

    public MotanxFrameworkException(Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(cause, motanErrorMsg);
    }

}
