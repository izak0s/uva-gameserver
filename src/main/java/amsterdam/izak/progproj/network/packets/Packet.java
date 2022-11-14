package amsterdam.izak.progproj.network.packets;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
    /**
     * Encode packet to bytes
     *
     * @return ByteBuf object
     */
    public abstract ByteBuf encode();

    public abstract void decode(ByteBuf buf);
}
