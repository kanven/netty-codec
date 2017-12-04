package com.kanven.netty.codec.demux;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.kanven.netty.Protocol;
import com.kanven.netty.Req;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.out.BytesStreamOutput;

public class ReqEncoder extends AbstractDemuxEncoder {

	public ReqEncoder(Serializer serializer) {
		super(serializer);
	}

	@Override
	protected void doEncode(BytesStreamOutput output, Protocol protocol) throws IOException {
		Req req = (Req) protocol;
		Map<String, String> attachments = req.getAttachments();
		int count = attachments == null ? 0 : attachments.size();
		output.writeInt(count);
		if (count > 0) {
			Set<String> keys = attachments.keySet();
			for (String key : keys) {
				String value = attachments.get(key);
				output.writeString(key);
				output.writeString(value);
			}
		}
	}

}
