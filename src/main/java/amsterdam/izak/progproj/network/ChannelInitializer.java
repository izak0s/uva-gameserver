package amsterdam.izak.progproj.network;

import io.netty.channel.socket.DatagramChannel;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<DatagramChannel> {

    @Override
    protected void initChannel(DatagramChannel ch) {
        System.out.println("Initializing pipeline");
        ch.pipeline()
                .addLast("encoder", new PacketEncoder())
                .addLast("decoder", new PacketDecoder())
                .addLast("handler", new PacketHandler());
    }
}
