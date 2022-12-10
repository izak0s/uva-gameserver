package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.UnknownPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.players.PlayerManager;
import amsterdam.izak.progproj.states.NetworkState;
import amsterdam.izak.progproj.utils.GameLog;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.DatagramPacket;

public class HandshakeHandler {
    public HandshakeHandler() {
        PacketManager packetManager = GameServer.getInstance().getPacketManager();
        PlayerManager playerManager = GameServer.getInstance().getPlayerManager();

        // Login request - 0x00
        packetManager.registerListener(LoginRequestPacket.class, packet -> {
            if (packet.getPlayer() != null)
                throw new Exception("Something unexpected happened");

            String username = packet.getPacket().getUsername();
            if (!playerManager.isUsernameAvailable(username)) {
                // Don't authenticate, send raw packet
                packetManager.sendRawPacket(
                        packet.getAddress(),
                        NetworkState.HANDSHAKE,
                        new LoginResponsePacket(false, "Username already in use")
                );

                return;
            }

            Player player = playerManager.registerPlayer(username, packet.getAddress());
            GameLog.info("Player " + username + " joined the server!");
            player.sendPacket(new LoginResponsePacket(true, ""));
            player.setState(NetworkState.GAME);

            // Update Game state
            GameServer.getInstance().getGamePlayHandler().playerJoins(player);
            GameServer.getInstance().getGamePlayHandler().updateState(player);
        });

        // Handle unknown packets
        packetManager.registerListener(UnknownPacket.class, packet -> {
            ByteBuf buf = Unpooled.buffer();
            // Let the client know about the unknown packet or invalid state
            buf.writeByte(100);

            DatagramPacket out = new DatagramPacket(buf, packet.getAddress());
            GameServer.getInstance().getChannel().writeAndFlush(out);

        });
    }
}
