package amsterdam.izak.progproj;

import amsterdam.izak.progproj.handlers.GamePacketHandler;
import amsterdam.izak.progproj.handlers.GamePlayHandler;
import amsterdam.izak.progproj.handlers.HandshakeHandler;
import amsterdam.izak.progproj.network.ChannelInitializer;
import amsterdam.izak.progproj.network.PacketManager;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.game.MovePlayerPacket;
import amsterdam.izak.progproj.players.Player;
import amsterdam.izak.progproj.players.PlayerManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameServer {
    @Getter
    private static GameServer instance;
    @Getter
    private final PacketManager packetManager;
    @Getter
    private final PlayerManager playerManager;
    @Getter
    private Channel channel;
    private EventLoopGroup workerGroup;
    private final int ticks = 20;
    private ScheduledFuture gameLoop;
    @Getter
    private GamePlayHandler gamePlayHandler;

    public GameServer() {
        GameServer.instance = this;
        this.packetManager = new PacketManager();
        this.playerManager = new PlayerManager();

        // Register packet handlers
        new HandshakeHandler();
        new GamePacketHandler();

        ScheduledExecutorService executor = Executors
                .newSingleThreadScheduledExecutor();

        this.gamePlayHandler = new GamePlayHandler();
        gameLoop = executor.scheduleAtFixedRate(gamePlayHandler::tick,
                0, 1000 / ticks, TimeUnit.MILLISECONDS);
    }

    public ChannelFuture start() {
        workerGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer());

        ChannelFuture channelFuture = bootstrap.bind("0.0.0.0", 1337)
                .syncUninterruptibly();
        this.channel = channelFuture.channel();

        return channelFuture;
    }

    public void stop() {
        gameLoop.cancel(true);
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
    }

    public void sendToAll(Packet packet) throws Exception {
        sendToAll(packet, null);
    }

    public void sendToAll(Packet packet, Player except) throws Exception {
        for (Player player : getPlayerManager().getPlayers()) {
            if (player == except)
                continue;

            player.sendPacket(packet);
        }
    }

    public void updateGame() {
        for (Player player : getPlayerManager().getPlayers()) {
            try {

            } catch (Exception e){
                System.out.println("Failed to update player " + player.getUsername());
            }
        }
    }
}
