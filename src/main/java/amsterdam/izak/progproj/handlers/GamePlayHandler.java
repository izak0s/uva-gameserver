package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.network.packets.game.*;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.players.Position;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class GamePlayHandler {
    private List<Platform> platforms;
    private List<Platform> defaultPlatform;
    private final List<Color> availableColors;
    private List<Color> assignedColors;
    private final byte size = 5;
    private GamePlayState state;
    private RoundState roundState;

    private float counter;
    private Color currentColor;
    private final Color defaultColor = new Color(52, 73, 94);
    private Set<Player> alive = new CopyOnWriteArraySet<>();
    private int roundNumber = 1;

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
        currentColor = defaultColor;

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

        try {
            Collection<Player> players = game().getPlayerManager().getPlayers();
            if (state.equals(GamePlayState.IDLE) && !players.isEmpty()) {
                state = GamePlayState.COUNTING_DOWN;
                counter = 10;
                System.out.println("Start counting down");
            } else if ((state.equals(GamePlayState.COUNTING_DOWN)
                    || state.equals(GamePlayState.RUNNING)) && players.isEmpty()) {
                state = GamePlayState.IDLE;
                System.out.println("Changing game state to idle");
                resetGame();
            }

            // Countdown
            if (state.equals(GamePlayState.COUNTING_DOWN)) {
                if (counter <= 0) {
                    state = GamePlayState.RUNNING;

                    randomizeMap();
                    game().sendToAll(new UpdateMapPacket(size, platforms));

                    System.out.println("Start game");

                    alive.addAll(game().getPlayerManager().getPlayers());

                    counter = 5f;
                    roundState = RoundState.WAITING_TO_START;

                    updateAllStates();
                } else {
                    counter -= (1f / 20f);

                    updateAllStates();
                }
            }

            // While running
            if (state.equals(GamePlayState.RUNNING)) {
                if (counter <= 0) {
                    switch (roundState) {
                        case WAITING_TO_START -> {
                            roundState = RoundState.WAITING_FOR_NEXT_ROUND;
                            game().sendToAll(new UpdateMapPacket(size, defaultPlatform));
                            counter = 1;
                            updateAllStates();
                        }
                        case WAITING_FOR_NEXT_ROUND -> {
                            roundNumber++;
                            roundState = RoundState.COLOR_ANNOUNCED;
                            counter = 3;
                            Random rand = new Random();
                            currentColor = availableColors.get(rand.nextInt(availableColors.size()));
                            updateAllStates();
                        }
                        case COLOR_ANNOUNCED -> {
                            roundState = RoundState.COLOR_FIXED;
                            counter = 3;
                            updateAllStates();
                            updatePlatforms();
                        }
                        case COLOR_FIXED -> {
                            roundState = RoundState.WAITING_FOR_NEXT_ROUND;
                            resetPlatforms();
                            game().sendToAll(new UpdateMapPacket(size, defaultPlatform));
                            counter = 3;
                            updateAllStates();
                        }
                        case WINNER -> {
                            resetGame();
                            game().sendToAll(new UpdateMapPacket(size, defaultPlatform));
                            updateAllStates();
                        }
                    }
                } else {
                    counter -= (1f / 20f);
                }
            }
            for (Player player : game().getPlayerManager().getPlayers()) {
                try {
                    // Remove idle players
                    if (now - player.getLastPacket() > 1000 * 10) {
                        game().getPlayerManager().removePlayer(player);
                        System.out.println("Player " + player.getUsername() + " left");
                        continue;
                    }

                    if (player.getPosition() != null && player.getPosition().getY() <= -10) {
                        switch (state) {
                            case COUNTING_DOWN, IDLE -> {
                                Position pos = new Position(0, 5, 0);
                                player.sendPacket(new MovePlayerPacket(-1, pos));
                            }
                            case RUNNING -> playerDie(player);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Failed to update player " + player.getUsername());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playerJoins(Player player) throws Exception {
        switch (state) {
            case IDLE, COUNTING_DOWN -> {
                // Add all players
                for (Player onlinePlayer : game().getPlayerManager().getPlayers())
                    if (player != onlinePlayer)
                        player.sendPacket(new AddPlayerPacket(
                                onlinePlayer.getId(),
                                onlinePlayer.getUsername(),
                                onlinePlayer.getPosition())
                        );

                // Let other players know about new player
                GameServer.getInstance().sendToAll(new AddPlayerPacket(
                        player.getId(),
                        player.getUsername(),
                        new Position(0, 0, 0)
                ));
            }

            case RUNNING -> {
                // Add alive players
                for (Player onlinePlayer : alive)
                    player.sendPacket(new AddPlayerPacket(
                            onlinePlayer.getId(),
                            onlinePlayer.getUsername(),
                            onlinePlayer.getPosition())
                    );

                // Spectate
                player.sendPacket(new SpectatePacket(true));
            }
        }

    }

    public void updatePlatforms() throws Exception {
        platforms.stream().filter(platform -> !platform.getColor().equals(currentColor)).forEach(platform -> platform.setActive(false));
        game().sendToAll(new UpdateMapPacket(size, platforms));
    }

    public void resetPlatforms() {
        platforms.forEach(platform -> platform.setActive(true));
    }

    private void updateAllStates() throws Exception {
        for (Player player : game().getPlayerManager().getPlayers())
            updateState(player);
    }

    public void updateState(Player player) throws Exception {
        if (state.equals(GamePlayState.IDLE)) {
            player.sendPacket(
                    new UpdateUIPacket(new Color(52, 73, 94), "Waiting")
            );

            alive.clear();
            player.sendPacket(new UpdateMapPacket(size, defaultPlatform));

            return;
        } else if (state.equals(GamePlayState.COUNTING_DOWN)) {
            int i = Math.round(counter * 2);
            player.sendPacket(
                    new UpdateUIPacket(new Color(52, 73, 94), "Waiting")
            );


            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);

            player.sendPacket(new UpdateTitlePacket("Waiting for players..", "Starting in: " + df.format(counter)));
        } else if (state.equals(GamePlayState.RUNNING)) {
            switch (roundState) {
                case WAITING_TO_START -> {
                    player.sendPacket(new UpdateTitlePacket("", ""));
                    player.sendPacket(new UpdateUIPacket(defaultColor, "Look around"));
                }
                case WAITING_FOR_NEXT_ROUND -> {
                    player.sendPacket(new UpdateUIPacket(defaultColor, "Round " + roundNumber));
                }
                case COLOR_ANNOUNCED -> {
                    player.sendPacket(new UpdateUIPacket(currentColor, "Go to color"));
                }
                case COLOR_FIXED -> {
                    player.sendPacket(new UpdateUIPacket(currentColor, "Wait"));
                }
                case WINNER -> {
                    Optional<Player> username = alive.stream().findFirst();
                    if (username.isEmpty())
                        return;
                    player.sendPacket(new UpdateTitlePacket(
                            "Player " + username.get().getUsername() + " won!",
                            "survived for " + roundNumber + " rounds!"));
                }
            }
        }
    }

    public void playerDie(Player player) throws Exception {
        if (!alive.contains(player))
            return;

        System.out.println("Player " + player.getUsername() + " died!");
        player.sendPacket(new SpectatePacket(true));

        if (alive.size() == 1) {
            System.out.println("Player " + player.getUsername() + " won!");
            roundState = RoundState.WINNER;
            counter = 5f;
            updateAllStates();
        }

        alive.remove(player);
        game().sendToAll(new RemovePlayerPacket(player.getId()), player);
    }

    public void resetGame() throws Exception {
        game().sendToAll(new UpdateMapPacket(size, defaultPlatform));
        game().sendToAll(new SpectatePacket(false));
        resetPlatforms();

        state = GamePlayState.IDLE;
        roundState = RoundState.WAITING_TO_START;
        roundNumber = 1;
        alive.clear();

        for (Player p : game().getPlayerManager().getPlayers())
            game().sendToAll(new AddPlayerPacket(p.getId(), p.getUsername(), new Position(0, 0, 0)));
    }

    public GameServer game() {
        return GameServer.getInstance();
    }
}
