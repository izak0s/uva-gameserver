package amsterdam.izak.progproj.network.packets;

import amsterdam.izak.progproj.states.NetworkState;
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

    public NetworkState getState(){
        return this.isAuthenticated() ? NetworkState.GAME : NetworkState.HANDSHAKE;
    }

    public boolean isAuthenticated(){
        return player != null;
    }

}
