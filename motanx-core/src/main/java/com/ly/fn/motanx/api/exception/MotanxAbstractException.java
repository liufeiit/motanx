package com.ly.fn.motanx.api.exception;

public abstract class MotanxAbstractException extends RuntimeException {
    private static final long serialVersionUID = -8742311167276890503L;

    protected MotanxErrorMsg motanErrorMsg = MotanxErrorMsgConstant.FRAMEWORK_DEFAULT_ERROR;
    protected String errorMsg = null;

    public MotanxAbstractException() {
        super();
    }

    public MotanxAbstractException(MotanxErrorMsg motanErrorMsg) {
        super();
        this.motanErrorMsg = motanErrorMsg;
    }

    public MotanxAbstractException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public MotanxAbstractException(String message, MotanxErrorMsg motanErrorMsg) {
        super(message);
        this.motanErrorMsg = motanErrorMsg;
        this.errorMsg = message;
    }

    public MotanxAbstractException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    public MotanxAbstractException(String message, Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(message, cause);
        this.motanErrorMsg = motanErrorMsg;
        this.errorMsg = message;
    }

    public MotanxAbstractException(Throwable cause) {
        super(cause);
    }

    public MotanxAbstractException(Throwable cause, MotanxErrorMsg motanErrorMsg) {
        super(cause);
        this.motanErrorMsg = motanErrorMsg;
    }

    @Override
    public String getMessage() {
        if (motanErrorMsg == null) {
            return super.getMessage();
        }

        String message;

        if (errorMsg != null && !"".equals(errorMsg)) {
            message = errorMsg;
        } else {
            message = motanErrorMsg.getMessage();
        }

        // TODO 统一上下文 requestid
        return "error_message: " + message + ", status: " + motanErrorMsg.getStatus() + ", error_code: " + motanErrorMsg.getErrorCode()
                + ",r=";
    }

    public int getStatus() {
        return motanErrorMsg != null ? motanErrorMsg.getStatus() : 0;
    }

    public int getErrorCode() {
        return motanErrorMsg != null ? motanErrorMsg.getErrorCode() : 0;
    }

    public MotanxErrorMsg getMotanErrorMsg() {
        return motanErrorMsg;
    }
}
