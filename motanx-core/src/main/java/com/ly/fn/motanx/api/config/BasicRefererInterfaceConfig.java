package com.ly.fn.motanx.api.config;

public class BasicRefererInterfaceConfig extends AbstractRefererConfig {

    private static final long serialVersionUID = -418351068816874749L;
    /** 是否默认配置 */
    private Boolean isDefault;

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Boolean isDefault() {
        return isDefault;
    }



}
