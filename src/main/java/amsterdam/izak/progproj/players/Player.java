package amsterdam.izak.progproj.players;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.InetSocketAddress;

@Getter
@RequiredArgsConstructor
public class Player {
    private final int id;
    private final InetSocketAddress address;
    private final String username;
    private long lastPacket = -1;


    public void updateLastPacket(){
        lastPacket = System.currentTimeMillis();
    }
}
