package com.kanven.netty.codec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.kanven.netty.Constants;
import com.kanven.netty.Protocol;
import com.kanven.netty.Req;
import com.kanven.netty.Resp;
import com.kanven.netty.codec.demux.DemuxEncoder;
import com.kanven.netty.codec.demux.ReqEncoder;
import com.kanven.netty.codec.demux.RespEncoder;
import com.kanven.netty.codec.serialize.Serializer;
import com.kanven.netty.codec.serialize.kryo.KryoSerializer;
import com.kanven.netty.codec.stream.out.BytesStreamOutput;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 
 * <b>协议编码规则</b>:
 * 
 * <pre>
 * <b>协议格式：</b>头部+内容
 * </pre>
 * 
 * <pre>
 * <b>头部：</b>魔数+版本号+类型+内容长度
 * </pre>
 * 
 * <pre>
 * <b>内容：</b>版本号+请求编号+类名称+长度+实体序列化内容+(attachments: len (key value)*)
 * </pre>
 * 
 * @author kanven
 *
 */
public class NettyEncoder extends MessageToByteEncoder<Protocol> {

	private final static Map<Byte, DemuxEncoder<Protocol>> demux = new HashMap<Byte, DemuxEncoder<Protocol>>();

	static {
		Serializer serializer = new KryoSerializer();
		demux.put(Constants.PROCOTOL_REQ_FLAG, new ReqEncoder(serializer));
		demux.put(Constants.PROTOCOL_RESP_FLAG, new RespEncoder(serializer));
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Protocol protocol, ByteBuf out) throws Exception {
		byte type = getType(protocol);
		DemuxEncoder<Protocol> encoder = demux.get(type);
		if (encoder == null) {
			throw new RuntimeException("没有指定类型(" + type + ")的编码器！");
		}
		byte[] data = encoder.encode(ctx, protocol);
		out.writeBytes(encodeHeader(protocol, data.length));
		out.writeBytes(data);
	}

	private byte[] encodeHeader(Protocol protocol, int length) throws IOException {
		BytesStreamOutput output = new BytesStreamOutput(Constants.PROTOCOL_HEADER_LENGTH);
		try {
			output.writeShort(Constants.PROTOCOL_MAGIC);
			output.writeByte(Constants.PROTOCOL_VERSION);
			output.writeByte(getType(protocol));
			output.writeInt(length);
			return output.getBytes();
		} finally {
			output.close();
		}
	}

	private byte getType(Protocol protocol) {
		if (protocol instanceof Req) {
			return Constants.PROCOTOL_REQ_FLAG;
		} else if (protocol instanceof Resp) {
			return Constants.PROTOCOL_RESP_FLAG;
		}
		return Constants.PROTOCOL_OTHER;
	}

}
