package com.ly.fn.motanx.api.rpc;

public class Application {

    private String application;
    private String module;

    public Application(String application, String module) {
        this.application = application;
        this.module = module;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

}
