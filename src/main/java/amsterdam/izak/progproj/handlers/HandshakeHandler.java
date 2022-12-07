package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.game.AddPlayerPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginRequestPacket;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.players.PlayerManager;
import amsterdam.izak.progproj.players.Position;

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
                        GameState.HANDSHAKE,
                        new LoginResponsePacket(false, "Username already in use")
                );

                return;
            }

            Player player = playerManager.registerPlayer(username, packet.getAddress());
            System.out.println("Player " + username + " joined the server!");
            player.sendPacket(new LoginResponsePacket(true, ""));
            player.setState(GameState.GAME);

            for (Player onlinePlayer : playerManager.getPlayers())
                if (player != onlinePlayer)
                    player.sendPacket(new AddPlayerPacket(onlinePlayer.getId(), onlinePlayer.getUsername(), onlinePlayer.getPosition()));

            // Update Game state
            GameServer.getInstance().getGamePlayHandler().updateState(player);

            AddPlayerPacket addPlayer = new AddPlayerPacket(player.getId(), player.getUsername(), new Position(0, 0, 0));
            GameServer.getInstance().sendToAll(addPlayer, player);
        });
    }
}
