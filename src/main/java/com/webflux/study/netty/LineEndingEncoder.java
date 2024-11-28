package com.webflux.study.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class LineEndingEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        if (msg != null) {
            ByteBuf encoded = ctx.alloc().buffer();
            encoded.writeCharSequence(msg + "\n", StandardCharsets.UTF_8);
            out.add(encoded);
        }
    }
}
