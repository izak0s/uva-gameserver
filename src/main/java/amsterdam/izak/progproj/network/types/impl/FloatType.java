package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.network.types.Type;
import io.netty.buffer.ByteBuf;

public class FloatType implements Type<Float> {
    @Override
    public void encode(ByteBuf buf, Float item) {
        buf.writeFloatLE(item);
    }

    @Override
    public Float decode(ByteBuf buf) throws Exception {
        return buf.readFloatLE();
    }
}
