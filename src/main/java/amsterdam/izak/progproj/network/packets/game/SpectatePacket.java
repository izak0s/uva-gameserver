package amsterdam.izak.progproj.network.packets.game;

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
public class SpectatePacket extends Packet  {
    private boolean enabled;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.BOOLEAN.encode(buf, this.enabled);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {

    }
}
