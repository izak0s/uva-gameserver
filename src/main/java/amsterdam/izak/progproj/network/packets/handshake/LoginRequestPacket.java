package amsterdam.izak.progproj.network.packets.handshake;

import amsterdam.izak.progproj.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class LoginRequestPacket extends Packet {
    private String username;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        username = Vars.STRING.decode(buf);
    }
}
