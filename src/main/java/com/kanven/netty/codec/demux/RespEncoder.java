package com.kanven.netty.codec.demux;

import java.io.IOException;

import com.kanven.netty.Protocol;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.out.BytesStreamOutput;

public class RespEncoder extends AbstractDemuxEncoder {

	public RespEncoder(Serializer serializer) {
		super(serializer);
	}

	@Override
	protected void doEncode(BytesStreamOutput output, Protocol protocol) throws IOException {
	}

}
