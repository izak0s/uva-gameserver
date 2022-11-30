package amsterdam.izak.progproj.network.packets.game;

import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class UpdateMapPacket extends Packet
{
    private byte size;
    private List<Platform> platforms;

    @Override
    public void encode(ByteBuf buf) throws Exception {
        if (size * size != platforms.size())
            throw new Exception("Platform size should match the given size");

        Vars.BYTE.encode(buf, size);
        platforms.forEach(platform -> Vars.PLATFORM.encode(buf, platform));
    }

    @Override
    public void decode(ByteBuf buf) throws Exception {
        throw new NotImplementedException();
    }
}
