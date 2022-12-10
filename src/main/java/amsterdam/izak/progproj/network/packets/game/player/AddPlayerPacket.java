package amsterdam.izak.progproj.network.packets.game.player;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import amsterdam.izak.progproj.data.Position;
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
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.INT, id);
        buf.write(Vars.STRING, username);
        buf.write(Vars.POSITION, position);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        throw new NotImplementedException();
    }
}
