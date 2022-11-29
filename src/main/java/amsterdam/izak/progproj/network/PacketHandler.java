package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import amsterdam.izak.progproj.network.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class PacketHandler extends SimpleChannelInboundHandler<IncomingPacketWrapper> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IncomingPacketWrapper item) throws Exception {
        System.out.println("Packet handler " + item);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
