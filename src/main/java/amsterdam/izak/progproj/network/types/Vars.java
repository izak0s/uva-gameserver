package amsterdam.izak.progproj.network.types;

import amsterdam.izak.progproj.network.types.impl.BoolType;
import amsterdam.izak.progproj.network.types.impl.ByteType;
import amsterdam.izak.progproj.network.types.impl.IntType;
import amsterdam.izak.progproj.network.types.impl.StringType;

public class Vars {
    public static final StringType STRING = new StringType();
    public static final IntType INT = new IntType();
    public static final ByteType BYTE = new ByteType();
    public static final BoolType BOOLEAN = new BoolType();
}
