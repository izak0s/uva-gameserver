package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.utils.GameLog;
import io.netty.channel.socket.DatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelInitializer extends io.netty.channel.ChannelInitializer<DatagramChannel> {

    private static Logger logger = LoggerFactory.getLogger(ChannelInitializer.class);

    @Override
    protected void initChannel(DatagramChannel ch) {
        GameLog.info("Initializing pipeline");
        ch.pipeline()
                .addLast("decoder", new PacketDecoder())
                .addLast("handler", new InboundPacketChannelHandler());
    }
}
