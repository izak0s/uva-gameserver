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
public class UpdateTitlePacket extends Packet  {
    private String title;
    private String subtitle;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.STRING.encode(buf, this.title);
        Vars.STRING.encode(buf, this.subtitle);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }
}
