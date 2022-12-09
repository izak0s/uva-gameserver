package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.game.ClientMovePacket;
import amsterdam.izak.progproj.network.packets.game.MovePlayerPacket;
import amsterdam.izak.progproj.players.PlayerManager;

public class GamePacketHandler {
    public GamePacketHandler() {
        PacketManager packetManager = GameServer.getInstance().getPacketManager();
        PlayerManager playerManager = GameServer.getInstance().getPlayerManager();

        // 0x01 - Client move
        packetManager.registerListener(ClientMovePacket.class, packet -> {
            // Update player position
            packet.getPacket().getPosition().setX(packet.getPacket().getPosition().getX() + 1);
            packet.getPlayer().setPosition(packet.getPacket().getPosition());
            GameServer.getInstance().sendToAll(
                    new MovePlayerPacket(
                            packet.getPlayer().getId(),
                            packet.getPacket().getPosition(),
                            packet.getPacket().getYaw()
                    )
//                    packet.getPlayer()
            );
        });
    }
}
