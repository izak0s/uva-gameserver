package amsterdam.izak.progproj.network;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class PacketDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket dg, List<Object> list) throws Exception {
        System.out.println("DECODE!");

        System.out.println(dg.sender());
        ByteBuf buf = dg.content();
        byte item = buf.readByte();
        System.out.println("Decoded byte: " + item);

        System.out.println("Readable" + buf.readableBytes());
        list.add(item);

        ByteBuf output = Unpooled.buffer();
        output.writeBytes("Kus".getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(new DatagramPacket(output, dg.sender()));
        System.out.println("Written");
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
