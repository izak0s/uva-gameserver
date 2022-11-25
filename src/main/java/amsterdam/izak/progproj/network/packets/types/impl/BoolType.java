package amsterdam.izak.progproj.network.packets.types.impl;

import amsterdam.izak.progproj.network.packets.types.Type;
import io.netty.buffer.ByteBuf;

public class BoolType implements Type<Boolean> {
    @Override
    public void encode(ByteBuf buf, Boolean item) {
        buf.writeBoolean(item);
    }

    @Override
    public Boolean decode(ByteBuf buf) {
        return buf.readBoolean();
    }
}
