package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.handlers.HandshakeHandler;
import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.game.*;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PacketManager {
    private Map<GameState, BidiMap<Byte, Class<? extends Packet>>> incoming_packets = new HashMap<>();
    private Map<GameState, BidiMap<Byte, Class<? extends Packet>>> outgoing_packets = new HashMap<>();
    private Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;

    public PacketManager() {
        this.handlers = new HashMap<>();

        // Handshake
        this.registerIn(GameState.HANDSHAKE, LoginRequestPacket.class);
        this.registerOut(GameState.HANDSHAKE, LoginResponsePacket.class);

        // Game
        this.registerIn(GameState.GAME, KeepAlivePacket.class)
                .registerIn(GameState.GAME, ClientMovePacket.class);
        this.registerOut(GameState.GAME, KeepAlivePacket.class)
                .registerOut(GameState.GAME, AddPlayerPacket.class)
                .registerOut(GameState.GAME, RemovePlayerPacket.class)
                .registerOut(GameState.GAME, MovePlayerPacket.class)
                .registerOut(GameState.GAME, UpdateMapPacket.class)
                .registerOut(GameState.GAME, UpdateUIPacket.class)
                .registerOut(GameState.GAME, SpectatePacket.class)
                .registerOut(GameState.GAME, UpdateTitlePacket.class);
    }

    private PacketManager registerIn(GameState state, Class<? extends Packet> packet) {
        if (!incoming_packets.containsKey(state))
            incoming_packets.put(state, new DualHashBidiMap<>());

        int size = this.incoming_packets.get(state).size();

        this.incoming_packets.get(state).put((byte) size, packet);

        return this;
    }

    private PacketManager registerOut(GameState state, Class<? extends Packet> packet) {
        if (!outgoing_packets.containsKey(state))
            outgoing_packets.put(state, new DualHashBidiMap<>());

        int size = this.outgoing_packets.get(state).size();

        this.outgoing_packets.get(state).put((byte) size, packet);

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

        if (packets == null || !packets.containsValue(packet.getClass())) {
            throw new Exception("Unknown packet with id" + packet);
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

    public <T extends Packet> void registerListener(Class<T> claz, PacketHandler<T> listener) {
        if (!this.handlers.containsKey(claz))
            this.handlers.put(claz, new HashSet<>());

        this.handlers.get(claz).add(listener);
    }

    public void handlePacket(IncomingPacketWrapper packet) {
        if (!this.handlers.containsKey(packet.getPacket().getClass())) {
            return;
        }

        this.handlers.get(packet.getPacket().getClass()).forEach(handler -> {
            try {
                handler.handle(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendRawPacket(InetSocketAddress address, GameState state, Packet packet) throws Exception {
        ByteBuf buf = encodePacket(state, packet);
        GameServer.getInstance().getChannel().writeAndFlush(new DatagramPacket(buf, address));
    }
}
