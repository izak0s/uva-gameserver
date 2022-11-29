package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {
    private Map<GameState, BidiMap<Byte, Class<? extends Packet>>> incoming_packets = new HashMap<>();
    private Map<GameState, BidiMap<Byte, Class<? extends Packet>>> outgoing_packets = new HashMap<>();

    public PacketManager() {
        this.registerIn(GameState.HANDSHAKE, LoginRequestPacket.class);

        this.registerOut(GameState.HANDSHAKE, LoginResponsePacket.class);
    }

    private PacketManager registerIn(GameState state, Class<? extends Packet> packet) {
        if (!incoming_packets.containsKey(state))
            incoming_packets.put(state, new DualHashBidiMap<>());

        int size = this.incoming_packets.get(state).size();

        this.incoming_packets.get(state).put((byte)size, packet);

        return this;
    }

    private PacketManager registerOut(GameState state, Class<? extends Packet> packet) {
        if (!outgoing_packets.containsKey(state))
            outgoing_packets.put(state, new DualHashBidiMap<>());

        int size = this.outgoing_packets.get(state).size();

        this.outgoing_packets.get(state).put((byte)size, packet);

        return this;
    }

    public Packet getPacket(GameState state, byte packetId) throws Exception {
        Map<Byte, Class<? extends Packet>> packets = incoming_packets.get(state);

        if (!packets.containsKey(packetId))
            throw new Exception("Unknown packet with id " + packetId);

        return packets.get(packetId).getDeclaredConstructor().newInstance();
    }

    public byte findOutgoingPacketId(GameState state, Packet packet) throws Exception {
        BidiMap<Byte, Class<? extends Packet>> packets = outgoing_packets.get(state);

        if (packets == null || packets.containsValue(packet.getClass())){
            throw new Exception("Unknown packet with id" + packets);
        }

        return packets.getKey(packet.getClass());
    }

    public ByteBuf encodePacket(GameState state, Packet packet) throws Exception {
        ByteBuf buf = Unpooled.buffer();

        byte packetId = findOutgoingPacketId(state, packet);
        buf.writeByte(packetId);

        // Encode packet
        packet.encode(buf);

        return buf;
    }
}
