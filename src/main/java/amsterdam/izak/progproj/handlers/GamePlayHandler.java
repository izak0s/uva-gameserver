package amsterdam.izak.progproj.handlers;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Position;
import amsterdam.izak.progproj.network.packets.game.env.UpdateTitlePacket;
import amsterdam.izak.progproj.network.packets.game.env.UpdateUIPacket;
import amsterdam.izak.progproj.network.packets.game.player.AddPlayerPacket;
import amsterdam.izak.progproj.network.packets.game.player.MovePlayerPacket;
import amsterdam.izak.progproj.network.packets.game.player.RemovePlayerPacket;
import amsterdam.izak.progproj.network.packets.game.player.SpectatePacket;
import amsterdam.izak.progproj.platforms.PlatformManager;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.states.GamePlayState;
import amsterdam.izak.progproj.states.RoundState;
import amsterdam.izak.progproj.utils.GameLog;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class GamePlayHandler {
    private final Set<Player> alive = new HashSet<>();
    private final PlatformManager platforms;
    private GamePlayState state;
    private RoundState roundState;
    private float counter;
    private int roundNumber = 0;
    private float timeVisible;
    private final DecimalFormat df;

    public GamePlayHandler() {
        this.state = GamePlayState.IDLE;
        platforms = GameServer.getInstance().getPlatformManager();
        timeVisible = 5f;

        // Init DecimalFormat
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    }

    public void tick() {
        long now = System.currentTimeMillis();

        try {
            Collection<Player> players = game().getPlayerManager().getPlayers();

            // Start countdown when players  are waiting
            if (state.equals(GamePlayState.IDLE) && !players.isEmpty()) {
                state = GamePlayState.COUNTING_DOWN;
                counter = 15;
                GameLog.debug("Start counting down");
            }
            // Change to idle when all players left
            else if ((state.equals(GamePlayState.COUNTING_DOWN)
                    || state.equals(GamePlayState.RUNNING)) && players.isEmpty()) {
                state = GamePlayState.IDLE;
                GameLog.debug("Changing game state to idle");
                resetGame();
            }

            // Countdown
            if (state.equals(GamePlayState.COUNTING_DOWN)) {
                if (counter <= 0) {
                    state = GamePlayState.RUNNING;
                    alive.addAll(game().getPlayerManager().getPlayers());

                    GameLog.debug("Start game");
                    platforms.randomizeMap().sendMap();

                    counter = timeVisible;
                    roundState = RoundState.SHOW_MAP;

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
                        case SHOW_MAP -> {
                            roundNumber++;
                            roundState = RoundState.HIDE_MAP_AFTER_SHOW;
                            platforms.sendDefaultMap();
                            counter = 1;
                            updateAllStates();
                        }
                        case HIDE_MAP_AFTER_SHOW -> {
                            roundState = RoundState.COLOR_ANNOUNCED;
                            counter = 3;

                            platforms.pickColor();
                            updateAllStates();
                        }
                        case COLOR_ANNOUNCED -> {
                            roundState = RoundState.COLOR_FIXED;
                            counter = 3;
                            updateAllStates();
                            platforms.pickPlatformsWithColor();
                        }
                        case COLOR_FIXED -> {
                            timeVisible -= .2f;
                            roundState = RoundState.SHOW_MAP;
                            platforms.resetPlatforms().randomizeMap().sendMap();
                            counter = 3;
                            updateAllStates();
                        }
                        case WINNER -> {
                            resetGame();
                            platforms.sendDefaultMap();
                            updateAllStates();
                        }
                    }
                } else {
                    counter -= (1f / 20f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (Player player : game().getPlayerManager().getPlayers()) {
                try {
                    // Remove idle players (longer than 10 seconds)
                    if (now - player.getLastPacket() > 1000 * 10) {
                        game().getPlayerManager().removePlayer(player);
                        continue;
                    }

                    //  Out of map detection
                    if (player.getPosition() != null && player.getPosition().getY() <= -10) {
                        switch (state) {
                            case COUNTING_DOWN, IDLE -> {
                                Position pos = new Position(0, 5, 0);
                                player.sendPacket(new MovePlayerPacket(-1, pos, 0));
                            }
                            case RUNNING -> playerDie(player);
                        }
                    }
                } catch (Exception e) {
                    GameLog.error("Failed to update player " + player.getUsername());
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
                        player.getPosition()
                ), player);
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
            platforms.sendDefaultMap();
        } else if (state.equals(GamePlayState.COUNTING_DOWN)) {
            if (counter <= 0) counter = 0;

            // Send packets
            player.sendPacket(new UpdateUIPacket(
                    new Color(52, 73, 94), "Waiting")
            );
            player.sendPacket(new UpdateTitlePacket(
                    "Waiting for players..",
                    "Starting in: " + df.format(counter)
            ));
        } else if (state.equals(GamePlayState.RUNNING)) {
            switch (roundState) {
                case SHOW_MAP -> {
                    player.sendPacket(new UpdateTitlePacket("", ""));
                    player.sendPacket(new UpdateUIPacket(platforms.defaultColor, "Look around"));
                }
                case HIDE_MAP_AFTER_SHOW -> {
                    player.sendPacket(new UpdateUIPacket(platforms.defaultColor, "Round " + roundNumber));
                }
                case COLOR_ANNOUNCED -> {
                    player.sendPacket(new UpdateUIPacket(platforms.getCurrentColor(), "Go to color"));
                }
                case COLOR_FIXED -> {
                    player.sendPacket(new UpdateUIPacket(platforms.getCurrentColor(), "Wait"));
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

        GameLog.info("Player " + player.getUsername() + " died!");
        player.sendPacket(new SpectatePacket(true));

        // Only one alive!
        if (alive.size() == 1) {
            GameLog.info("Player " + player.getUsername() + " won!");
            roundState = RoundState.WINNER;
            counter = 5f;
            updateAllStates();
        }

        alive.remove(player);
        game().sendToAll(new RemovePlayerPacket(player.getId()), player);
    }

    public void resetGame() throws Exception {
        // Disable spectator mode & reset map
        game().sendToAll(new SpectatePacket(false));
        platforms.resetPlatforms().sendDefaultMap();

        // Reset parameters
        state = GamePlayState.IDLE;
        roundState = RoundState.SHOW_MAP;
        roundNumber = 0;
        timeVisible = 5f;
        alive.clear();

        // Spawn all players back
        for (Player p : game().getPlayerManager().getPlayers())
            game().sendToAll(new AddPlayerPacket(p.getId(), p.getUsername(), p.getPosition()), p);
    }

    public GameServer game() {
        return GameServer.getInstance();
    }
}
