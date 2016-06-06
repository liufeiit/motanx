package com.ly.fn.motanx.api.rpc;

public abstract class AbstractExporter<T> extends AbstractNode implements Exporter<T> {
    protected Provider<T> provider;

    public AbstractExporter(Provider<T> provider, URL url) {
        super(url);
        this.provider = provider;
    }

    public Provider<T> getProvider() {
        return provider;
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getSimpleName() + "] url=" + url;
    }
}
