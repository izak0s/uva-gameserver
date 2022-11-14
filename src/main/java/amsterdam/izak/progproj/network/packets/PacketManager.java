package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {
    private Map<GameState, Class<? extends Packet>[]> incoming_packets = new ConcurrentHashMap<>();
    private Map<GameState, Class<? extends Packet>[]> outgoing_packets = new ConcurrentHashMap<>();

    public PacketManager() {
        incoming_packets.put(GameState.HANDSHAKE, new Class[]{LoginRequestPacket.class});
        outgoing_packets.put(GameState.HANDSHAKE, new Class[]{LoginResponsePacket.class});
    }
}
