package amsterdam.izak.progproj.network;


import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.packets.GamePacket;
import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.UnknownPacket;
import amsterdam.izak.progproj.network.types.Vars;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.states.NetworkState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket dg, List<Object> list) throws Exception {
        ByteBuf buf = dg.content();

        // Early return empty packet
        if (buf.readableBytes() <= 0)
            return;

        Player player = GameServer.getInstance().getPlayerManager().getPlayer(dg.sender());
        NetworkState state = player == null ? NetworkState.HANDSHAKE : NetworkState.GAME;

        // Decode packet ID
        byte packet_id = Vars.BYTE.decode(buf);
        Packet packet = GameServer.getInstance().getPacketManager()
                .getPacket(state, packet_id);

        // Handle unknown packet
        if (packet == null) {
            if (player == null) {
                list.add(new IncomingPacketWrapper<>(null, dg.sender(), new UnknownPacket()));

                return;
            }

            throw new Exception("Unknown packet with id " + packet_id);
        }

        try {
            // Decode packet
            packet.decode(new GamePacket(buf));
        } catch (IndexOutOfBoundsException e) {
            // Malformed unauthenticated packet
            if (player == null) {
                list.add(new IncomingPacketWrapper<>(null, dg.sender(), new UnknownPacket()));

                return;
            }

            throw e;
        }

        // Too many bytes packet
        if (buf.readableBytes() != 0) {
            if (player == null) {
                list.add(new IncomingPacketWrapper<>(null, dg.sender(), new UnknownPacket()));

                return;
            }

            throw new Exception("Malformed packet: " + packet_id);
        }

        list.add(new IncomingPacketWrapper(player, dg.sender(), packet));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
