package amsterdam.izak.progproj.network.types;

import amsterdam.izak.progproj.exceptions.NotImplementedException;
import io.netty.buffer.ByteBuf;

public interface Type<T>
{
    void encode(ByteBuf buf, T item);

    T decode(ByteBuf buf) throws Exception;
}
