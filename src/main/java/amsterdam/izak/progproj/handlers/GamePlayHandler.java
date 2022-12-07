package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.network.packets.game.UpdateMapPacket;
import amsterdam.izak.progproj.network.packets.game.UpdateUIPacket;
import amsterdam.izak.progproj.players.Player;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class GamePlayHandler {
    private List<Platform> platforms;
    private List<Platform> defaultPlatform;
    private final List<Color> availableColors;
    private List<Color> assignedColors;
    private int i = 0;
    private final byte size = 5;
    private GamePlayState state;

    private float countDown;
    private Color currentColor;

    public GamePlayHandler() {
        this.platforms = new ArrayList<>();
        this.defaultPlatform = new ArrayList<>();
        this.availableColors = new ArrayList<>();
        this.assignedColors = new ArrayList<>();
        this.state = GamePlayState.IDLE;

        initColors();

        for (int i = 0; i < size * size; i++) {
            platforms.add(new Platform(true, new Color(52, 73, 94)));
            defaultPlatform.add(new Platform(true, new Color(52, 73, 94)));
        }
    }

    public void initColors() {
        availableColors.add(new Color(52, 152, 219));
        availableColors.add(new Color(46, 204, 113));
        availableColors.add(new Color(231, 76, 60));
        availableColors.add(new Color(243, 156, 18));
        availableColors.add(new Color(142, 68, 173));
        currentColor = new Color(52, 73, 94);

        for (int i = 0; i < size * size; i++) {
            assignedColors.add(availableColors.get(i % size));
        }
    }

    public void randomizeMap() {
        Collections.shuffle(assignedColors);

        for (int i = 0; i < platforms.size(); i++) {
            platforms.get(i).setColor(assignedColors.get(i));
        }
    }


    public void tick() {
        long now = System.currentTimeMillis();
        i++;

        try {
//            if (i % 100 == 0) {
//                randomizeMap();
//                GameServer.getInstance().sendToAll(new UpdateMapPacket(size, platforms));
//
//                int blub = new Random().nextInt(0, size);
//                GameServer.getInstance().sendToAll(new UpdateUIPacket(availableColors.get(blub), "Test"));
//            }

            Collection<Player> players = game().getPlayerManager().getPlayers();
            if (state.equals(GamePlayState.IDLE) && !players.isEmpty()) {
                state = GamePlayState.COUNTING_DOWN;
                countDown = 10;
                System.out.println("Start counting down");
            } else if ((state.equals(GamePlayState.COUNTING_DOWN)
                    || state.equals(GamePlayState.RUNNING)) && players.isEmpty()) {
                state = GamePlayState.IDLE;
                System.out.println("Changing game state to idle");
            }

            if (state.equals(GamePlayState.COUNTING_DOWN)) {
                if (countDown <= 0) {
                    state = GamePlayState.RUNNING;

                    randomizeMap();
                    GameServer.getInstance().sendToAll(new UpdateMapPacket(size, platforms));
                    // TODO start game
                    System.out.println("Start game");
                    updateAllStates();

                } else {
                    countDown -= (1f / 20f);
                    updateAllStates();
                }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAllStates() throws Exception {
        for (Player player : GameServer.getInstance().getPlayerManager().getPlayers())
            updateState(player);
    }

    public void updateState(Player player) throws Exception {
        if (state.equals(GamePlayState.IDLE)) {
            GameServer.getInstance().sendToAll(
                    new UpdateUIPacket(new Color(52, 73, 94), "Waiting")
            );

            GameServer.getInstance().sendToAll(new UpdateMapPacket(size, defaultPlatform));

            return;
        } else if (state.equals(GamePlayState.COUNTING_DOWN)) {
            int i = Math.round(countDown * 2);
            Color c = i % 2 == 0 ? new Color(52, 73, 94) : new Color(52, 152, 219);

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            GameServer.getInstance().sendToAll(new UpdateUIPacket(c, df.format(countDown)));
        } else if (state.equals(GamePlayState.RUNNING)) {
            GameServer.getInstance().sendToAll(new UpdateUIPacket(currentColor, "Look around"));
        }
    }

    public GameServer game() {
        return GameServer.getInstance();
    }
}
