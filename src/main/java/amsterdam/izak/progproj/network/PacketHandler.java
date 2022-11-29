package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import amsterdam.izak.progproj.network.packets.Packet;

public interface PacketHandler<T extends Packet> {
    void handle(IncomingPacketWrapper<T> packet) throws Exception;
}
