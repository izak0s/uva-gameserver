package amsterdam.izak.progproj.network.packets.handshake;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class LoginResponsePacket extends Packet {
    private boolean authenticated;
    private String message;

    @Override
    public void encode(GamePacket buf) {
        buf.write(Vars.BOOLEAN, authenticated);
        buf.write(Vars.STRING, message);
    }

    @Override
    public void decode(GamePacket buf) throws NotImplementedException {
        throw new NotImplementedException();
    }
}

