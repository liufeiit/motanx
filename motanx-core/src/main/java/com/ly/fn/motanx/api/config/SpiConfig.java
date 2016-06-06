package com.ly.fn.motanx.api.config;

public class SpiConfig<T> {

    private String id;
    
    private Class<T> interfaceClass;
    
    private Class<T> spiClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public Class<T> getSpiClass() {
        return spiClass;
    }

    public void setSpiClass(Class<T> spiClass) {
        this.spiClass = spiClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}