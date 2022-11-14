package amsterdam.izak.progproj.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class PacketHandler extends SimpleChannelInboundHandler<Byte> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Byte item) throws Exception {
        System.out.println("Packet handler " + item);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
