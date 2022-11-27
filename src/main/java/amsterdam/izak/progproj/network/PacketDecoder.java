package amsterdam.izak.progproj.network;


import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.network.packets.Packet;
import amsterdam.izak.progproj.network.packets.handshake.LoginResponsePacket;
import amsterdam.izak.progproj.network.types.Vars;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket dg, List<Object> list) throws Exception {
        System.out.println(dg.sender());
        ByteBuf buf = dg.content();

        // Early return empty packet
        if (buf.readableBytes() <= 0)
            return;

        byte packet_id = Vars.BYTE.decode(buf);
        Packet packet = GameServer.getInstance().getPacketManager()
                .getPacket(GameState.HANDSHAKE, packet_id);

        System.out.println(buf.readableBytes() + " READABLE");

        // Decode packet
        packet.decode(buf);

        if (buf.readableBytes() != 0){
            throw new Exception("Malformed packet");
        }

        System.out.println("Received packet" + packet.getClass().getName());
        list.add(packet);

        ByteBuf output = Unpooled.buffer();
        Packet out = new LoginResponsePacket(true, "Kusje");
        output.writeByte(0x00);
        out.encode(output);
//        output.writeBytes("Kus".getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(new DatagramPacket(output, dg.sender()));
//        System.out.println("Written");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    //
//    @Override
//    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        System.out.println("DECODER!");
//        System.out.println("DEC byte" + byteBuf.readByte());
//        byte item = byteBuf.readByte();
//
//        list.add(item);
//    }
}
