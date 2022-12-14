package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.game.env.UpdateMapPacket;
import amsterdam.izak.progproj.network.packets.game.env.UpdateTitlePacket;
import amsterdam.izak.progproj.network.packets.game.env.UpdateUIPacket;
import amsterdam.izak.progproj.network.packets.game.logic.KeepAlivePacket;
import amsterdam.izak.progproj.network.packets.game.player.*;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import amsterdam.izak.progproj.states.NetworkState;
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
import java.util.concurrent.ConcurrentHashMap;

public class PacketManager {
    private final Map<NetworkState, BidiMap<Byte, Class<? extends Packet>>> incoming_packets = new ConcurrentHashMap<>();
    private final Map<NetworkState, BidiMap<Byte, Class<? extends Packet>>> outgoing_packets = new ConcurrentHashMap<>();
    private final Map<Class<? extends Packet>, Set<PacketHandler<? extends Packet>>> handlers;

    public PacketManager() {
        this.handlers = new HashMap<>();

        // Handshake
        this.registerIn(NetworkState.HANDSHAKE, LoginRequestPacket.class);
        this.registerOut(NetworkState.HANDSHAKE, LoginResponsePacket.class);

        // Game
        this.registerIn(NetworkState.GAME, KeepAlivePacket.class)
                .registerIn(NetworkState.GAME, ClientMovePacket.class)
                .registerIn(NetworkState.GAME, QuitPacket.class);
        this.registerOut(NetworkState.GAME, KeepAlivePacket.class)
                .registerOut(NetworkState.GAME, AddPlayerPacket.class)
                .registerOut(NetworkState.GAME, RemovePlayerPacket.class)
                .registerOut(NetworkState.GAME, MovePlayerPacket.class)
                .registerOut(NetworkState.GAME, UpdateMapPacket.class)
                .registerOut(NetworkState.GAME, UpdateUIPacket.class)
                .registerOut(NetworkState.GAME, SpectatePacket.class)
                .registerOut(NetworkState.GAME, UpdateTitlePacket.class);
    }

    private PacketManager registerIn(NetworkState state, Class<? extends Packet> packet) {
        if (!incoming_packets.containsKey(state))
            incoming_packets.put(state, new DualHashBidiMap<>());

        int size = this.incoming_packets.get(state).size();
        this.incoming_packets.get(state).put((byte) size, packet);

        return this;
    }

    private PacketManager registerOut(NetworkState state, Class<? extends Packet> packet) {
        if (!outgoing_packets.containsKey(state))
            outgoing_packets.put(state, new DualHashBidiMap<>());

        int size = this.outgoing_packets.get(state).size();
        this.outgoing_packets.get(state).put((byte) size, packet);

        return this;
    }

    public Packet getPacket(NetworkState state, byte packetId) throws Exception {
        Map<Byte, Class<? extends Packet>> packets = incoming_packets.get(state);

        if (!packets.containsKey(packetId))
            return null;

        return packets.get(packetId).getDeclaredConstructor().newInstance();
    }

    public byte findOutgoingPacketId(NetworkState state, Packet packet) throws Exception {
        BidiMap<Byte, Class<? extends Packet>> packets = outgoing_packets.get(state);

        if (packets == null || !packets.containsValue(packet.getClass())) {
            throw new Exception("Unknown packet with id" + packet);
        }

        return packets.getKey(packet.getClass());
    }

    public ByteBuf encodePacket(NetworkState state, Packet packet) throws Exception {
        ByteBuf buf = Unpooled.buffer();

        byte packetId = findOutgoingPacketId(state, packet);
        buf.writeByte(packetId);

        GamePacket gameBuf = new GamePacket(buf);

        // Encode packet
        packet.encode(gameBuf);

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

    public void sendRawPacket(InetSocketAddress address, NetworkState state, Packet packet) throws Exception {
        ByteBuf buf = encodePacket(state, packet);
        GameServer.getInstance().getChannel().writeAndFlush(new DatagramPacket(buf, address));
    }
}
