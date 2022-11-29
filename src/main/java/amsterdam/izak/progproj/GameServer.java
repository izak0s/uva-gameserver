package amsterdam.izak.progproj;

import amsterdam.izak.progproj.handlers.GamePacketHandler;
import amsterdam.izak.progproj.handlers.HandshakeHandler;
import amsterdam.izak.progproj.network.ChannelInitializer;
import amsterdam.izak.progproj.network.PacketManager;
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
    public GameServer() {
        GameServer.instance = this;
        this.packetManager = new PacketManager();
        this.playerManager = new PlayerManager();

        // Register packet handlers
        new HandshakeHandler();
        new GamePacketHandler();

        Timer t = new Timer();
        ScheduledExecutorService executor = Executors
                .newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::updateGame,
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
        if (channel != null) {
            channel.close();
        }
        workerGroup.shutdownGracefully();
    }

    public void updateGame(){
        getPlayerManager().getPlayers().forEach(player -> {
            if (player.getPosition() != player.getLastSentPosition()){
                System.out.println("Updated position");
                player.setLastSentPosition(player.getPosition());
            }
        });
    }
}
