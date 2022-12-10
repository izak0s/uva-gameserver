package amsterdam.izak.progproj;

import amsterdam.izak.progproj.network.ChannelInitializer;
import amsterdam.izak.progproj.utils.GameLog;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgrammeerProject {
    public static void main(String[] args) throws InterruptedException {
        GameServer server = new GameServer();
        ChannelFuture future = server.start();

        GameLog.info("Server started on port 1337");
        // Wait until the connection is closed.
        future.channel().closeFuture().sync();
    }
}
