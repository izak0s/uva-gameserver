package amsterdam.izak.progproj.network.packets.game;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RemovePlayerPacket  extends Packet {
    private int id;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.INT.encode(buf, id);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }
}
