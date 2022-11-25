package amsterdam.izak.progproj.network.types;

import io.netty.buffer.ByteBuf;

public interface Type<T>
{
    void encode(ByteBuf buf, T item);

    T decode(ByteBuf buf);
}
