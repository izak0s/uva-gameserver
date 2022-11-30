package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.players.Player;

public class GamePlayHandler {
    public GamePlayHandler(){

    }

    public void tick(){
        long now = System.currentTimeMillis();
        for (Player player : game().getPlayerManager().getPlayers()) {
            try {
                if (now - player.getLastPacket() > 1000 * 10) {
                    game().getPlayerManager().removePlayer(player);
                    System.out.println("Player " + player.getUsername() + " left");
                }
            } catch (Exception e){
                System.out.println("Failed to update player " + player.getUsername());
            }
        }
    }

    public GameServer game(){
        return GameServer.getInstance();
    }
}
