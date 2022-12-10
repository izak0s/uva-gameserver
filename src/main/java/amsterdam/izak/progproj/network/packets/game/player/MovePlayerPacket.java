package amsterdam.izak.progproj.network.packets.game.player;

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
public class MovePlayerPacket extends Packet {
    private int id;
    private Position position;
    private float yaw;

    @Override
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.INT, id);
        buf.write(Vars.POSITION, position);
        buf.write(Vars.FLOAT, yaw);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        id = buf.read(Vars.INT);
        position = buf.read(Vars.POSITION);
        yaw = buf.read(Vars.FLOAT);
    }
}
