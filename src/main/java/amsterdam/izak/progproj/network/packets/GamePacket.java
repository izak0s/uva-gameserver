package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.network.types.Type;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GamePacket {
    @Getter
    private ByteBuf buf;

    public <T> T read(Type<T> type) throws Exception {
        return type.decode(buf);
    }

    public <T> GamePacket write(Type<T> type, T value) {
        type.encode(buf, value);

        return this;
    }
}
