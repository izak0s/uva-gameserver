package amsterdam.izak.progproj.network.packets.game;

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
public class MovePlayerPacket extends Packet {
    private int id;
    private Position position;
    private float yaw;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.INT.encode(buf, id);
        Vars.POSITION.encode(buf, position);
        Vars.FLOAT.encode(buf, yaw);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        id = Vars.INT.decode(buf);
        position = Vars.POSITION.decode(buf);
        yaw = Vars.FLOAT.decode(buf);
    }
}
