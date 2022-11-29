package amsterdam.izak.progproj.network;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.packets.IncomingPacketWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class InboundPacketChannelHandler extends SimpleChannelInboundHandler<IncomingPacketWrapper> {
    private PacketManager manager;

    public InboundPacketChannelHandler(){
        this.manager = GameServer.getInstance().getPacketManager();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IncomingPacketWrapper item) throws Exception {
        System.out.println("Packet handler " + item);
        manager.handlePacket(item);

        // Update last packet
        if (item.getPlayer() != null)
            item.getPlayer().updateLastPacket();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
