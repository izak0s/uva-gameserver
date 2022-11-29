package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.players.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

@Getter
@AllArgsConstructor
public class IncomingPacketWrapper<T extends Packet> {
    private Player player;
    private InetSocketAddress address;
    private T packet;

    public GameState getState(){
        return this.isAuthenticated() ? GameState.GAME : GameState.HANDSHAKE;
    }

    public boolean isAuthenticated(){
        return player != null;
    }

}
