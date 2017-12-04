package com.kanven.netty.codec.demux;

import java.io.IOException;
import java.util.List;

import com.kanven.netty.Constants;
import com.kanven.netty.Protocol;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.stream.in.BytesStreamInput;

public abstract class AbstractDemuxDecoder<T extends Protocol> implements DemuxDecoder {

	private final Serializer serializer;

	public AbstractDemuxDecoder(Serializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void decode(byte[] data, List<Object> out) throws Exception {
		BytesStreamInput input = new BytesStreamInput(data);
		try {
			byte version = input.readByte();
			if (version > Constants.PROTOCOL_BODY_VERSION) {
				throw new RuntimeException(
						String.format("版本不一致，当前版本：%s，实际版本：%s ", Constants.PROTOCOL_BODY_VERSION, version));
			}
			T protocol = buildProtocol();
			if (protocol == null) {
				throw new RuntimeException("协议实体不能为空！");
			}
			long seq = input.readLong();
			protocol.setSeq(seq);
			int len = input.readInt();
			byte[] body = new byte[len];
			input.read(body);
			protocol.setBody(serializer.deserializ(body));
			doDecode(input, protocol);
			out.add(protocol);
		} finally {
			input.close();
		}
	}

	protected abstract void doDecode(BytesStreamInput input, T procotol) throws IOException;

	protected abstract T buildProtocol();

}
