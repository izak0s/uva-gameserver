package amsterdam.izak.progproj.network.types.impl;

import amsterdam.izak.progproj.network.types.Type;
import amsterdam.izak.progproj.data.Position;
import io.netty.buffer.ByteBuf;

public class PositionType implements Type<Position> {
    @Override
    public void encode(ByteBuf buf, Position item) {
        buf.writeFloatLE(item.getX());
        buf.writeFloatLE(item.getY());
        buf.writeFloatLE(item.getZ());
    }

    @Override
    public Position decode(ByteBuf buf) {
        Position pos = new Position();

        pos.setX(buf.readFloatLE());
        pos.setY(buf.readFloatLE());
        pos.setZ(buf.readFloatLE());

        return pos;
    }
}
