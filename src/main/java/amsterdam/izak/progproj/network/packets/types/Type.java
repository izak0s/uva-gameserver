package amsterdam.izak.progproj.network.packets.types;

import io.netty.buffer.ByteBuf;

public interface Type<T>
{
    void encode(ByteBuf buf, T item);

    T decode(ByteBuf buf);
}
