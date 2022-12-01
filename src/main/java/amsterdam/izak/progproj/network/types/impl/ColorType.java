package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.network.types.Type;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;

import java.awt.*;

public class ColorType implements Type<Color> {
    @Override
    public void encode(ByteBuf buf, Color item) {
        Vars.INT.encode(buf, item.getRed());
        Vars.INT.encode(buf, item.getGreen());
        Vars.INT.encode(buf, item.getBlue());
    }

    @Override
    public Color decode(ByteBuf buf) throws Exception {
        int r = Vars.INT.decode(buf);
        int g = Vars.INT.decode(buf);
        int b = Vars.INT.decode(buf);
        return new Color(r, g, b);
    }
}
