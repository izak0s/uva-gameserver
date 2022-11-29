package amsterdam.izak.progproj.network.packets.game;

import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import amsterdam.izak.progproj.players.Position;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Vector;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClientMovePacket extends Packet {
    private Position position;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.POSITION.encode(buf, position);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        position = Vars.POSITION.decode(buf);
    }
}
