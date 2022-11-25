package amsterdam.izak.progproj;

import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.network.packets.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;

public class GameUser {
    @Getter
    private final InetSocketAddress address;
    @Setter
    @Getter
    private String name;

    public GameUser(InetSocketAddress address) {
        this.address = address;
        this.name = null;
    }

    public void sendPacket(Packet packet) {

    }
}
