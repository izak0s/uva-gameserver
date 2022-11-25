package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.NotImplementedException;
import io.netty.buffer.ByteBuf;

public abstract class Packet {
    /**
     * Encode packet to bytes
     */
    public abstract void encode(ByteBuf buf) throws Exception;

    public abstract void decode(ByteBuf buf) throws Exception;
}
