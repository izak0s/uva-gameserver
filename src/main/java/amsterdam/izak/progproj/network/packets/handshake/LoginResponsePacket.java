package amsterdam.izak.progproj.network.packets.handshake;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponsePacket extends Packet {
    private boolean authenticated;
    private String message;

    @Override
    public void encode(ByteBuf buf) {
        Vars.BOOLEAN.encode(buf, authenticated);
        Vars.STRING.encode(buf, message);
    }

    @Override
    public void decode(ByteBuf buf) throws NotImplementedException {
        throw new NotImplementedException();
    }
}

