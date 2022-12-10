package amsterdam.izak.progproj.network.packets;

public abstract class Packet {
    /**
     * Encode packet to bytes
     */
    public abstract void encode(GamePacket buf) throws Exception;

    public abstract void decode(GamePacket buf) throws Exception;
}
