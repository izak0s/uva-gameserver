package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.network.packets.game.UpdateMapPacket;
import amsterdam.izak.progproj.players.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GamePlayHandler {
    private List<Platform> platforms;
    private List<Platform> defaultPlatform;
    private final List<Color> availableColors;
    private List<Color> assignedColors;
    private int i = 0;
    private final byte size = 5;

    public GamePlayHandler(){
        this.platforms = new ArrayList<>();
        this.defaultPlatform = new ArrayList<>();
        this.availableColors = new ArrayList<>();
        this.assignedColors = new ArrayList<>();

        initColors();

        for (int i = 0; i < size * size; i++) {
            platforms.add(new Platform(true, new Color(52, 73, 94)));
            defaultPlatform.add(new Platform(true, new Color(52, 73, 94)));
        }
    }

    public void initColors(){
        availableColors.add(new Color(52, 152, 219));
        availableColors.add(new Color(46, 204, 113));
        availableColors.add(new Color(231, 76, 60));
        availableColors.add(new Color(243, 156, 18));
        availableColors.add(new Color(142, 68, 173));

        for (int i = 0; i < size * size; i++){
            assignedColors.add(availableColors.get(i % size));
        }
    }

    public void randomizeMap(){
        Collections.shuffle(assignedColors);

        for (int i = 0; i < platforms.size(); i++) {
            platforms.get(i).setColor(assignedColors.get(i));
        }
    }


    public void tick(){
        long now = System.currentTimeMillis();
        i++;

        try {
            if (i % 100 == 0) {
                randomizeMap();
                GameServer.getInstance().sendToAll(new UpdateMapPacket(size, platforms));
            }

            for (Player player : game().getPlayerManager().getPlayers()) {
                try {
                    if (now - player.getLastPacket() > 1000 * 10) {
                        game().getPlayerManager().removePlayer(player);
                        System.out.println("Player " + player.getUsername() + " left");
                    }
                } catch (Exception e) {
                    System.out.println("Failed to update player " + player.getUsername());
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public GameServer game(){
        return GameServer.getInstance();
    }
}
