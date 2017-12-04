package com.kanven.netty.codec.serialize.kryo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.kanven.netty.codec.serialize.Serializer;

/**
 * KRYO高性能序列化实现
 * 
 * @author kanven
 * 
 */
public class KryoSerializer implements Serializer {

	@Override
	public byte[] serialize(Object o) throws Exception {
		if (o == null) {
			return null;
		}
		ByteArrayOutputStream bos = null;
		Output output = null;
		try {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			bos = new ByteArrayOutputStream();
			output = new Output(bos);
			kryo.writeClassAndObject(output, o);
			output.flush();
			return bos.toByteArray();
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					throw new Exception("序列化失败！", e);
				}
			}
			if (output != null) {
				output.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserializ(byte[] bts) throws Exception {
		if (bts == null || bts.length <= 0) {
			return null;
		}
		ByteArrayInputStream bis = null;
		Input input = null;
		try {
			Kryo kryo = new Kryo();
			kryo.setReferences(false);
			bis = new ByteArrayInputStream(bts);
			input = new Input(bis);
			return (T) kryo.readClassAndObject(input);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					throw new Exception("反序列化出现异常！", e);
				}
			}
			if (input != null) {
				input.close();
			}
		}
	}

}
