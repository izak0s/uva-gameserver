package amsterdam.izak.progproj.network.types;

import amsterdam.izak.progproj.network.types.impl.*;
import amsterdam.izak.progproj.players.Position;

public class Vars {
    public static final StringType STRING = new StringType();
    public static final IntType INT = new IntType();
    public static final ByteType BYTE = new ByteType();
    public static final BoolType BOOLEAN = new BoolType();

    public static final PositionType POSITION = new PositionType();
    public static final PlatformType PLATFORM = new PlatformType();
}
