package amsterdam.izak.progproj.network.packets.handshake;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LoginRequestPacket extends Packet {
    private String username;

    @Override
    public void encode(GamePacket buf) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        username = buf.read(Vars.STRING);
    }
}
