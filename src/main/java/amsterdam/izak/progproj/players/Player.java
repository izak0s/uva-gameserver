package amsterdam.izak.progproj.players;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Position;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.states.NetworkState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

@Getter
public class Player {
    private final int id;
    private final InetSocketAddress address;
    private final String username;
    private long lastPacket = -1;
    private NetworkState currentState;
    @Setter
    @Getter
    private Position position;

    public Player(int id, InetSocketAddress address, String username) {
        this.id = id;
        this.address = address;
        this.username = username;
        this.currentState = NetworkState.HANDSHAKE;
        this.position = new Position(0, 0, 0);
        this.updateLastPacket();
    }

    public void updateLastPacket() {
        lastPacket = System.currentTimeMillis();
    }

    public void setState(NetworkState state) {
        this.currentState = state;
    }

    public void sendPacket(Packet packet) throws Exception {
        ByteBuf buf = GameServer.getInstance().getPacketManager().encodePacket(currentState, packet);

        GameServer.getInstance().getChannel().writeAndFlush(new DatagramPacket(buf, address));
    }
}
