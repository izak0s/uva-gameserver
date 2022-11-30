package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.exceptions.NotImplementedException;
import amsterdam.izak.progproj.network.types.Type;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;

public class PlatformType implements Type<Platform> {
    @Override
    public void encode(ByteBuf buf, Platform item) {
        Vars.BOOLEAN.encode(buf, item.isActive());
        Vars.INT.encode(buf, item.getColor().getRed());
        Vars.INT.encode(buf, item.getColor().getGreen());
        Vars.INT.encode(buf, item.getColor().getBlue());
    }

    @Override
    public Platform decode(ByteBuf buf) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
