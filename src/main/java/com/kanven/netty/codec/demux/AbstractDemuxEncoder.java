package com.kanven.netty.codec.demux;

import java.io.IOException;

import com.kanven.netty.Constants;
import com.kanven.netty.Protocol;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.out.BytesStreamOutput;

import io.netty.channel.ChannelHandlerContext;

public abstract class AbstractDemuxEncoder implements DemuxEncoder<Protocol> {

	private final Serializer serializer;

	public AbstractDemuxEncoder(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public byte[] encode(ChannelHandlerContext ctx, Protocol protocol) throws Exception {
		BytesStreamOutput output = new BytesStreamOutput();
		try {
			output.writeByte(Constants.PROTOCOL_BODY_VERSION);
			output.writeLong(protocol.getSeq());
			byte[] bodys = serializer.serialize(protocol.getBody());
			output.writeInt(bodys == null ? 0 : bodys.length);
			output.write(bodys);
			doEncode(output, protocol);
			return output.getBytes();
		} finally {
			output.close();
		}
	}

	protected abstract void doEncode(BytesStreamOutput output, Protocol protocol) throws IOException;

}
