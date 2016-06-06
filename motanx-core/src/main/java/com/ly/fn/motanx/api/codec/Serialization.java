package com.ly.fn.motanx.api.codec;

import java.io.IOException;

import com.ly.fn.motanx.api.core.extension.Scope;
import com.ly.fn.motanx.api.core.extension.Spi;

@Spi(scope = Scope.PROTOTYPE)
public interface Serialization {

    byte[] serialize(Object obj) throws IOException;

    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;
}
