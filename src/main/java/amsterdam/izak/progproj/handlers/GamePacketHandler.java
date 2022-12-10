package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.game.player.ClientMovePacket;
import amsterdam.izak.progproj.network.packets.game.player.MovePlayerPacket;
import amsterdam.izak.progproj.network.packets.game.player.QuitPacket;
import amsterdam.izak.progproj.players.PlayerManager;

public class GamePacketHandler {
    public GamePacketHandler() {
        PacketManager packetManager = GameServer.getInstance().getPacketManager();
        PlayerManager playerManager = GameServer.getInstance().getPlayerManager();

        // 0x01 - Client move
        packetManager.registerListener(ClientMovePacket.class, packet -> {
            // Update player position
            packet.getPlayer().setPosition(packet.getPacket().getPosition());

            // Ignore packets far into the void
            if (packet.getPacket().getPosition().getY() < -50)
                return;

            GameServer.getInstance().sendToAll(
                    new MovePlayerPacket(
                            packet.getPlayer().getId(),
                            packet.getPacket().getPosition(),
                            packet.getPacket().getYaw()
                    ),
                    packet.getPlayer()
            );
        });

        // 0x02 - Quit packet
        packetManager.registerListener(QuitPacket.class, packet -> {
            playerManager.removePlayer(packet.getPlayer());
        });
    }
}
