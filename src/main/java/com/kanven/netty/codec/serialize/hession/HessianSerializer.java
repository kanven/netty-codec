package com.kanven.netty.codec.serialize.hession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.kanven.netty.codec.serialize.Serializer;

public class HessianSerializer implements Serializer {

	@Override
	public byte[] serialize(Object o) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		HessianOutput ho = new HessianOutput(os);
		try {
			ho.writeObject(o);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		return os.toByteArray();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserializ(byte[] bts) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(bts);
		HessianInput hi = new HessianInput(is);
		try {
			return (T) hi.readObject();
		} catch (IOException e) {
			throw e;
		}
	}

}
