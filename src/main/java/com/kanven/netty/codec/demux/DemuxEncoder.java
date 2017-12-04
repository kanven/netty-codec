package com.kanven.netty.codec.demux;

import io.netty.channel.ChannelHandlerContext;

public interface DemuxEncoder<T> {

	byte[] encode(ChannelHandlerContext ctx,T protocol) throws Exception;
	
}
