package amsterdam.izak.progproj;

import io.netty.channel.ChannelFuture;

public class ProgrammeerProject {

    public static void main(String[] args) throws InterruptedException {
        GameServer server = new GameServer(1337);
        ChannelFuture future = server.start();

        System.out.println("Server started on port 1337");
        // Wait until the connection is closed.
        future.channel().closeFuture().sync();
    }
}
