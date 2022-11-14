package amsterdam.izak.progproj.network.packets.handshake;

import amsterdam.izak.progproj.network.packets.Packet;
import io.netty.buffer.ByteBuf;

public class LoginResponsePacket extends Packet {
    @Override
    public ByteBuf encode() {
        return null;
    }

    @Override
    public void decode(ByteBuf buf) {

    }
}
