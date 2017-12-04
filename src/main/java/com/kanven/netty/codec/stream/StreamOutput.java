package com.kanven.netty.codec.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class StreamOutput extends OutputStream {

	public abstract void writeByte(byte b) throws IOException;

	public abstract void writeBytes(byte[] b, int offset, int length) throws IOException;

	public long position() {
		throw new UnsupportedOperationException("position operation is unsupported.");
	}

	public void reset() throws IOException {
		throw new UnsupportedOperationException("reset operation is unsupported.");
	}

	@Override
	public void write(int b) throws IOException {
		writeByte((byte) b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (b != null && b.length > 0) {
			writeBytes(b, 0, b.length);
		}
	}

	public final void writeShort(short v) throws IOException {
		writeByte((byte) (v >> 8));
		writeByte((byte) v);
	}

	public final void writeInt(int v) throws IOException {
		writeByte((byte) (v >> 24));
		writeByte((byte) (v >> 16));
		writeByte((byte) (v >> 8));
		writeByte((byte) v);
	}

	/**
	 * 整型以可变长写入，可为负数，负数长度为5。优势：值越小，占用空间越小
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void writeVInt(int v) throws IOException {
		while ((v & ~0x7F) != 0) {
			writeByte((byte) ((v & 0x7f) | 0x80));
			v >>>= 7;
		}
		writeByte((byte) v);
	}

	public void writeIntArray(int[] value) throws IOException {
		writeVInt(value.length);
		for (int i = 0; i < value.length; i++) {
			writeInt(value[i]);
		}
	}

	public final void writeLong(long v) throws IOException {
		writeInt((int) (v >> 32));
		writeInt((int) v);
	}

	/**
	 * 长整型以可变长格式写入，不能为负数
	 * 
	 * @param v
	 * @throws IOException
	 */
	public void writeVLong(long v) throws IOException {
		assert v >= 0;
		while ((v & ~0x7F) != 0) {
			writeByte((byte) ((v & 0x7f) | 0x80));
			v >>>= 7;
		}
		writeByte((byte) v);
	}

	public void writeLongArray(long[] value) throws IOException {
		writeVInt(value.length);
		for (int i = 0; i < value.length; i++) {
			writeLong(value[i]);
		}
	}

	public final void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public void writeFloatArray(float[] value) throws IOException {
		writeVInt(value.length);
		for (int i = 0; i < value.length; i++) {
			writeFloat(value[i]);
		}
	}

	public final void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	public void writeDoubleArray(double[] value) throws IOException {
		writeVInt(value.length);
		for (int i = 0; i < value.length; i++) {
			writeDouble(value[i]);
		}
	}

	private static byte ZERO = 0;

	private static byte ONE = 1;

	private static byte TWO = 2;

	public void writeBoolean(boolean b) throws IOException {
		writeByte(b ? ONE : ZERO);
	}

	public void writeOptionalBoolean(Boolean b) throws IOException {
		if (b == null) {
			writeByte(TWO);
		} else {
			writeByte(b ? ONE : ZERO);
		}
	}

	public void writeString(String str) throws IOException {
		int charCount = str.length();
		writeVInt(charCount);
		int c;
		for (int i = 0; i < charCount; i++) {
			c = str.charAt(i);
			if (c <= 0x007F) {
				writeByte((byte) c);
			} else if (c > 0x07FF) {
				writeByte((byte) (0xE0 | c >> 12 & 0x0F));
				writeByte((byte) (0x80 | c >> 6 & 0x3F));
				writeByte((byte) (0x80 | c >> 0 & 0x3F));
			} else {
				writeByte((byte) (0xC0 | c >> 6 & 0x1F));
				writeByte((byte) (0x80 | c >> 0 & 0x3F));
			}
		}
	}

	public void writeStringArray(String[] array) throws IOException {
		writeVInt(array.length);
		for (String s : array) {
			writeString(s);
		}
	}

	public void writeMap(Map<String, ?> map) throws IOException {
		writeGenericValue(map);
	}

	public void writeGenericValue(Object o) throws IOException {
		if (o == null) {
			writeByte((byte) -1);
			return;
		}
		if (o instanceof String) {
			writeByte((byte) 0);
			writeString((String) o);
		} else if (o instanceof Integer) {
			writeByte((byte) 1);
			writeInt((Integer) o);
		} else if (o instanceof Long) {
			writeByte((byte) 2);
			writeLong((Long) o);
		} else if (o instanceof Float) {
			writeByte((byte) 3);
			writeFloat((Float) o);
		} else if (o instanceof Double) {
			writeByte((byte) 4);
			writeDouble((Double) o);
		} else if (o instanceof Boolean) {
			writeByte((byte) 5);
			writeBoolean((Boolean) o);
		} else if (o instanceof Byte) {
			writeByte((byte) 6);
			writeByte((Byte) o);
		} else if (o instanceof Short) {
			writeByte((byte) 7);
			writeShort((Short) o);
		} else if (o instanceof Date) {
			writeByte((byte) 8);
			Date d = (Date) o;
			writeLong(d.getTime());
		} else if (o instanceof int[]) {
			writeByte((byte) 10);
			writeIntArray((int[]) o);
		} else if (o instanceof long[]) {
			writeByte((byte) 11);
			writeLongArray((long[]) o);
		} else if (o instanceof float[]) {
			writeByte((byte) 12);
			writeFloatArray((float[]) o);
		} else if (o instanceof double[]) {
			writeByte((byte) 13);
			writeDoubleArray((double[]) o);
		} else if (o instanceof Object[]) {
			writeByte((byte) 14);
			Object[] objs = (Object[]) o;
			writeVInt(objs.length);
			for (Object obj : objs) {
				writeGenericValue(obj);
			}
		} else if (o instanceof List) {
			writeByte((byte) 21);
			List<?> l = (List<?>) o;
			writeVInt(l.size());
			for (Object obj : l) {
				writeGenericValue(obj);
			}
		} else if (o instanceof Set) {
			writeByte((byte) 22);
			Set<?> s = (Set<?>) o;
			writeVInt(s.size());
			for (Object obj : s) {
				writeGenericValue(obj);
			}
		} else if (o instanceof Map) {
			if (o instanceof HashMap) {
				writeByte((byte) 23);
			} else {
				writeByte((byte) 24);
			}
			@SuppressWarnings("unchecked")
			Map<String, ?> map = (Map<String, ?>) o;
			writeVInt(map.size());
			for (Entry<String, ?> entry : map.entrySet()) {
				writeString(entry.getKey());
				writeGenericValue(entry.getValue());
			}
		} else {
			throw new IOException("Can't write type [" + o.getClass() + "]");
		}
	}

}
