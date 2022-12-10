package amsterdam.izak.progproj.network.packets.game.player;

import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class SpectatePacket extends Packet {
    private boolean enabled;

    @Override
    public void encode(GamePacket buf) throws Exception {
        buf.write(Vars.BOOLEAN, this.enabled);
    }

    @Override
    public void decode(GamePacket buf) throws Exception {

    }
}
