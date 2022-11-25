package amsterdam.izak.progproj.network.packets.types.impl;

import amsterdam.izak.progproj.network.packets.types.Type;
import io.netty.buffer.ByteBuf;

public class IntType implements Type<Integer> {
    @Override
    public void encode(ByteBuf buf, Integer item) {
        buf.writeInt(item);
    }

    @Override
    public Integer decode(ByteBuf buf) {
        return buf.readInt();
    }
}
