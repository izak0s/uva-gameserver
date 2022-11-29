package amsterdam.izak.progproj.network.packets.game;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import amsterdam.izak.progproj.players.Position;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class AddPlayerPacket extends Packet  {
    private int id;
    private String username;
    private Position position;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.INT.encode(buf, id);
        Vars.STRING.encode(buf, username);
        Vars.POSITION.encode(buf, position);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }
}
