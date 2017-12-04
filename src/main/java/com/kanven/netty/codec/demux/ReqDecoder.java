package com.kanven.netty.codec.demux;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kanven.netty.Req;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.in.BytesStreamInput;

public class ReqDecoder extends AbstractDemuxDecoder<Req> {

	public ReqDecoder(Serializer serializer) {
		super(serializer);
	}

	@Override
	protected Req buildProtocol() {
		return new Req();
	}

	@Override
	protected void doDecode(BytesStreamInput input, Req req) throws IOException {
		int count = input.readInt();
		if (count > 0) {
			Map<String, String> attachments = new HashMap<String, String>();
			for (int i = 0; i < count; i++) {
				attachments.put(input.readString(), input.readString());
			}
			req.setAttachments(attachments);
		}
	}

}
