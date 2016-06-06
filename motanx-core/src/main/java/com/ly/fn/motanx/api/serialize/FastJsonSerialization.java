package com.ly.fn.motanx.api.serialize;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ly.fn.motanx.api.codec.Serialization;
import com.ly.fn.motanx.api.core.extension.SpiMeta;

/**
 * fastjson 序列化
 * 
 * <pre>
 * 对于嵌套场景无法支持
 * </pre>
 */
@SpiMeta(name = "fastjson")
public class FastJsonSerialization implements Serialization {

    @Override
    public byte[] serialize(Object data) throws IOException {
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.config(SerializerFeature.WriteClassName, true);
        serializer.write(data);
        return out.toBytes(null);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        return JSON.parseObject(new String(data), clz);
    }
}
