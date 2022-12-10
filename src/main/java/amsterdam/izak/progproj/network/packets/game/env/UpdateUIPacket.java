package amsterdam.izak.progproj.network.packets.game.env;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
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
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.COLOR, color);
        buf.write(Vars.STRING, text);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        throw new NotImplementedException();
    }
}
