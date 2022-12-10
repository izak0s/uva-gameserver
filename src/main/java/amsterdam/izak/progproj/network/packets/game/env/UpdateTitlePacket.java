package amsterdam.izak.progproj.network.packets.game.env;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
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
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.STRING, this.title);
        buf.write(Vars.STRING, this.subtitle);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        throw new NotImplementedException();
    }
}
