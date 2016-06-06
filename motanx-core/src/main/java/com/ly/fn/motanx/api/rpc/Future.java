package com.ly.fn.motanx.api.rpc;

public interface Future {
    /**
     * cancle the task
     * 
     * @return
     */
    boolean cancel();

    /**
     * task cancelled
     * 
     * @return
     */
    boolean isCancelled();

    /**
     * task is complete : normal or exception
     * 
     * @return
     */
    boolean isDone();

    /**
     * isDone() & normal
     * 
     * @return
     */
    boolean isSuccess();

    /**
     * if task is success, return the result.
     * 
     * @throws Exception when timeout, cancel, onFailure
     * @return
     */
    Object getValue();

    /**
     * if task is done or cancle, return the exception
     * 
     * @return
     */
    Exception getException();

    /**
     * add future listener , when task is success，failure, timeout, cancel, it will be called
     * 
     * @param listener
     */
    void addListener(FutureListener listener);

}
