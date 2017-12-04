package com.kanven.netty.codec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kanven.netty.Constants;
import com.kanven.netty.codec.demux.DemuxDecoder;
import com.kanven.netty.codec.demux.ReqDecoder;
import com.kanven.netty.codec.demux.RespDecoder;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.serialize.kryo.KryoSerializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class NettyDecoder extends ByteToMessageDecoder {

	private static final Map<Byte, DemuxDecoder> demux = new HashMap<Byte, DemuxDecoder>();

	static {
		Serializer serializer = new KryoSerializer();
		demux.put(Constants.PROTOCOL_RESP_FLAG, new RespDecoder(serializer));
		demux.put(Constants.PROCOTOL_REQ_FLAG, new ReqDecoder(serializer));
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (!checkHeaderAvailability(in)) {
			return;
		}
		in.markReaderIndex();
		checkProtocolLegality(in);
		byte type = in.readByte();
		int len = in.readInt();
		if (in.readableBytes() < len) {
			in.resetReaderIndex();
			return;
		}
		DemuxDecoder decoder = demux.get(type);
		if (decoder == null) {
			throw new RuntimeException("decoder unsupport type=" + type);
		}
		byte[] data = new byte[len];
		in.readBytes(data);
		decoder.decode(data, out);
	}

	private boolean checkHeaderAvailability(ByteBuf in) {
		int counts = in.readableBytes();
		return counts < Constants.PROTOCOL_HEADER_LENGTH ? false : true;
	}

	private void checkProtocolLegality(ByteBuf in) {
		short magic = in.readShort();
		if (magic != Constants.PROTOCOL_MAGIC) {
			in.resetReaderIndex();
			throw new RuntimeException("协议不支持" + magic + "类型！");
		}
		byte version = in.readByte();
		if (version > Constants.PROTOCOL_VERSION) {
			in.resetReaderIndex();
			throw new RuntimeException(String.format("版本不一致，当前版本：%s，实际版本：%s", Constants.PROTOCOL_VERSION, version));
		}
	}

}
