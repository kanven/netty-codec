package com.kanven.netty.codec.serialize;

/**
 * 序列化接口
 * 
 * @author kanven
 * 
 */
public interface Serializer {

	public byte[] serialize(Object o) throws Exception;

	public <T> T deserializ(byte[] bts) throws Exception;

}
