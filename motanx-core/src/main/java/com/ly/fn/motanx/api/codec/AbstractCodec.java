package com.ly.fn.motanx.api.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.ly.fn.motanx.api.exception.MotanxErrorMsgConstant;
import com.ly.fn.motanx.api.exception.MotanxFrameworkException;

public abstract class AbstractCodec implements Codec {
    protected void serialize(ObjectOutput output, Object message, Serialization serialize) throws IOException {
        if (message == null) {
            output.writeObject(null);
            return;
        }

        output.writeObject(serialize.serialize(message));
    }

    protected Object deserialize(byte[] value, Class<?> type, Serialization serialize) throws IOException {
        if (value == null) {
            return null;
        }

        return serialize.deserialize(value, type);
    }

    public ObjectOutput createOutput(OutputStream outputStream) {
        try {
            return new ObjectOutputStream(outputStream);
        } catch (Exception e) {
            throw new MotanxFrameworkException(this.getClass().getSimpleName() + " createOutput error", e, MotanxErrorMsgConstant.FRAMEWORK_ENCODE_ERROR);
        }
    }

    public ObjectInput createInput(InputStream in) {
        try {
            return new ObjectInputStream(in);
        } catch (Exception e) {
            throw new MotanxFrameworkException(this.getClass().getSimpleName() + " createInput error", e, MotanxErrorMsgConstant.FRAMEWORK_DECODE_ERROR);
        }
    }

}
