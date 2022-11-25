package amsterdam.izak.progproj.network.packets.types;

import amsterdam.izak.progproj.network.packets.types.impl.BoolType;
import amsterdam.izak.progproj.network.packets.types.impl.IntType;
import amsterdam.izak.progproj.network.packets.types.impl.StringType;

public class Vars {
    public static final StringType STRING = new StringType();
    public static final IntType INT = new IntType();
    public static final BoolType BOOLEAN = new BoolType();
}
