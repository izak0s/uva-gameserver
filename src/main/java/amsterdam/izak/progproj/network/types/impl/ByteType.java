package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.network.types.Type;
import io.netty.buffer.ByteBuf;

public class ByteType implements Type<Byte> {
    @Override
    public void encode(ByteBuf buf, Byte item) {
        buf.writeByte(item);
    }

    @Override
    public Byte decode(ByteBuf buf) {
        return buf.readByte();
    }
}
