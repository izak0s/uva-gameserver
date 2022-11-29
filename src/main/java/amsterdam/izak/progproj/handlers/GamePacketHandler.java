package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.game.ClientMovePacket;
import amsterdam.izak.progproj.players.PlayerManager;

public class GamePacketHandler {
    public GamePacketHandler() {
        PacketManager packetManager = GameServer.getInstance().getPacketManager();
        PlayerManager playerManager = GameServer.getInstance().getPlayerManager();

        // 0x01 - Client move
        packetManager.registerListener(ClientMovePacket.class, packet -> {
            System.out.println("Incoming move packet");
            System.out.println(packet);

            // Update player position
            packet.getPlayer().setPosition(packet.getPacket().getPosition());
        });
    }
}
