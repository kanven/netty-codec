package com.kanven.netty.codec.serialize.jdk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.kanven.netty.codec.serialize.Serializer;

/**
 * JAVA自身序列化实现
 * 
 * @author kanven
 * 
 */
public class JDKSerializer implements Serializer {

	@Override
	public byte[] serialize(Object o) throws Exception {
		ObjectOutputStream objectOutputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		try {
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(o);
			byte[] bytes = byteArrayOutputStream.toByteArray();
			return bytes;
		} finally {
			try {
				if (null != byteArrayOutputStream) {
					byteArrayOutputStream.close();
				}
			} catch (IOException e) {
				throw new Exception("序列化IO关闭出现异常！", e);
			}
			try {
				if (null != objectOutputStream) {
					objectOutputStream.close();
				}
			} catch (IOException ex) {
				throw new Exception("序列化IO关闭出现异常！", ex);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserializ(byte[] bts) throws Exception {
		if (bts == null || bts.length <= 0) {
			return null;
		}
		ByteArrayInputStream byteArrayInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			byteArrayInputStream = new ByteArrayInputStream(bts);
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			T result = (T) objectInputStream.readObject();
			return result;
		} finally {
			try {
				if (null != byteArrayInputStream) {
					byteArrayInputStream.close();
				}
			} catch (IOException e) {
				throw new Exception("反序列化IO关闭异常！", e);
			}
			try {
				if (null != objectInputStream) {
					objectInputStream.close();
				}
			} catch (Exception ex) {
				throw new Exception("反序列化IO关闭异常！", ex);
			}
		}
	}

}
