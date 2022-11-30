package amsterdam.izak.progproj.players;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.GameState;
import amsterdam.izak.progproj.network.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
public class Player {
    private final int id;
    private final InetSocketAddress address;
    private final String username;
    private long lastPacket = -1;
    private GameState currentState;
    @Setter
    @Getter
    private Position position;

    public Player(int id, InetSocketAddress address, String username) {
        this.id = id;
        this.address = address;
        this.username = username;
        this.currentState = GameState.HANDSHAKE;
        this.updateLastPacket();
    }

    public void updateLastPacket(){
        lastPacket = System.currentTimeMillis();
    }

    public void setState(GameState state){
        this.currentState = state;
    }

    public void sendPacket(Packet packet) throws Exception {
        ByteBuf buf = GameServer.getInstance().getPacketManager().encodePacket(currentState, packet);

        // TODO should bytebuf be freed
        GameServer.getInstance().getChannel().writeAndFlush(new DatagramPacket(buf, address));
    }
}
