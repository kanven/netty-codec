package com.kanven.netty.codec.demux;

import java.io.IOException;

import com.kanven.netty.Resp;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.in.BytesStreamInput;

public class RespDecoder extends AbstractDemuxDecoder<Resp> {

	public RespDecoder(Serializer serializer) {
		super(serializer);
	}

	@Override
	protected Resp buildProtocol() {
		return new Resp();
	}

	@Override
	protected void doDecode(BytesStreamInput input, Resp procotol) throws IOException {
	}

}
