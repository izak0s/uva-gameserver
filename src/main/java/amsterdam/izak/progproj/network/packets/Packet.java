package amsterdam.izak.progproj.network.packets;

import io.netty.buffer.ByteBuf;

public abstract class Packet {
    /**
     * Encode packet to bytes
     */
    public abstract void encode(GamePacket buf) throws Exception;

    public abstract void decode(GamePacket buf) throws Exception;
}
