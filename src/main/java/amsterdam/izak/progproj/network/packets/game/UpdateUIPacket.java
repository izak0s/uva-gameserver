package amsterdam.izak.progproj.network.packets.game;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateUIPacket extends Packet {
    private Color color;
    private String text;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        Vars.COLOR.encode(buf, color);
        Vars.STRING.encode(buf, text);
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }
}
