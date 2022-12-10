package amsterdam.izak.progproj.network.packets.game.env;

import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateMapPacket extends Packet {
    private byte size;
    private List<Platform> platforms;

    @Override
    public void encode(GamePacket buf) throws Exception {
        if (size * size != platforms.size())
            throw new Exception("Platform size should match the given size");

        buf.write(Vars.BYTE, size);
        platforms.forEach(platform -> buf.write(Vars.PLATFORM, platform));
    }

    @Override
    public void decode(GamePacket buf) throws Exception {
        throw new NotImplementedException();
    }
}
