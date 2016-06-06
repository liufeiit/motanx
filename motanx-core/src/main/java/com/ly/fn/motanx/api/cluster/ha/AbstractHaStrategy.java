package com.ly.fn.motanx.api.cluster.ha;

import com.ly.fn.motanx.api.cluster.HaStrategy;
import com.ly.fn.motanx.api.rpc.URL;

public abstract class AbstractHaStrategy<T> implements HaStrategy<T> {

    protected URL url;

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

}
