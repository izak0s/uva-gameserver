package amsterdam.izak.progproj.network.packets.game.player;

import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import amsterdam.izak.progproj.data.Position;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClientMovePacket extends Packet {
    private Position position;
    private float yaw;

    @Override
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.POSITION, position);
        buf.write(Vars.FLOAT, yaw);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        position = buf.read(Vars.POSITION);
        yaw = buf.read(Vars.FLOAT);
    }
}
