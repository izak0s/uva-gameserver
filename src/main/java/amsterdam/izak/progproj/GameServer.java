package amsterdam.izak.progproj;

import amsterdam.izak.progproj.network.ChannelInitializer;
import amsterdam.izak.progproj.network.packets.PacketManager;
import amsterdam.izak.progproj.players.PlayerManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;

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

    public GameServer() {
        GameServer.instance = this;
        this.packetManager = new PacketManager();
        this.playerManager = new PlayerManager();
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

}
