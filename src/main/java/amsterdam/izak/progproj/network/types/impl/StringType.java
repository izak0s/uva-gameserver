package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.network.types.Type;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class StringType implements Type<String> {
    @Override
    public void encode(ByteBuf buf, String item) {
        byte[] b = item.getBytes(StandardCharsets.UTF_8);

        Vars.INT.encode(buf, b.length);
        buf.writeBytes(b);
    }

    @Override
    public String decode(ByteBuf buf) {
        int len = Vars.INT.decode(buf);

        String item = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
        buf.skipBytes(len);

        return item;
    }
}
