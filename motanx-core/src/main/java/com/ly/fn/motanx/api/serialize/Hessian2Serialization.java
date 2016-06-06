package com.ly.fn.motanx.api.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.ly.fn.motanx.api.codec.Serialization;
import com.ly.fn.motanx.api.core.extension.SpiMeta;

/**
 * hession2 序列化，要求序列化的对象实现 java.io.Serializable 接口
 */
@SpiMeta(name = "hessian2")
public class Hessian2Serialization implements Serialization {

    @Override
    public byte[] serialize(Object data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Hessian2Output out = new Hessian2Output(bos);
        out.writeObject(data);
        out.flush();
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        return (T) input.readObject(clz);
    }
}
