package amsterdam.izak.progproj.players;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.packets.game.player.RemovePlayerPacket;
import amsterdam.izak.progproj.utils.GameLog;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    private final Map<Integer, Player> playerMap;
    private final Map<InetSocketAddress, Integer> connectionIdMap;
    private final Map<String, Integer> usernameIdMap;
    private int nextId;

    public PlayerManager() {
        playerMap = new ConcurrentHashMap<>();
        connectionIdMap = new ConcurrentHashMap<>();
        usernameIdMap = new ConcurrentHashMap<>();
        nextId = 1;
    }

    public boolean isUsernameAvailable(String username) {
        return !usernameIdMap.containsKey(username);
    }

    public Player registerPlayer(String username, InetSocketAddress address) {
        int id = nextId++;
        Player player = new Player(id, address, username);

        playerMap.put(id, player);
        connectionIdMap.put(address, id);
        usernameIdMap.put(username, id);

        return player;
    }

    public boolean authenticated(InetSocketAddress address) {
        return connectionIdMap.containsKey(address);
    }

    public Player getPlayer(InetSocketAddress address) {
        if (!connectionIdMap.containsKey(address))
            return null;

        int id = connectionIdMap.get(address);

        return playerMap.get(id);
    }

    public Collection<Player> getPlayers() {
        return this.playerMap.values();
    }

    public void removePlayer(Player player) throws Exception {
        GameLog.info(player.getUsername() + " left");
        RemovePlayerPacket removePlayerPacket = new RemovePlayerPacket(player.getId());
        GameServer.getInstance().sendToAll(removePlayerPacket);
        GameServer.getInstance().getGamePlayHandler().playerDie(player);

        playerMap.remove(player.getId());
        usernameIdMap.remove(player.getUsername());
        connectionIdMap.remove(player.getAddress());
    }
}
