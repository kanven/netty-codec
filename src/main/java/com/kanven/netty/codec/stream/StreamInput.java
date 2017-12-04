package com.kanven.netty.codec.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class StreamInput extends InputStream {

	public abstract byte readByte() throws IOException;

	public abstract void readBytes(byte[] b, int offset, int len) throws IOException;

	public byte[] readByteArray() throws IOException {
		int length = readVInt();
		byte[] values = new byte[length];
		for (int i = 0; i < length; i++) {
			values[i] = readByte();
		}
		return values;
	}

	public short readShort() throws IOException {
		return (short) (((readByte() & 0xFF) << 8) | (readByte() & 0xFF));
	}

	public int readInt() throws IOException {
		return ((readByte() & 0xFF) << 24) | ((readByte() & 0xFF) << 16) | ((readByte() & 0xFF) << 8)
				| (readByte() & 0xFF);
	}

	public int readVInt() throws IOException {
		byte b = readByte();
		int i = b & 0x7F;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7F) << 7;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7F) << 14;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7F) << 21;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		assert (b & 0x80) == 0;
		return i | ((b & 0x7F) << 28);
	}

	public int[] readIntArray() throws IOException {
		int length = readVInt();
		int[] values = new int[length];
		for (int i = 0; i < length; i++) {
			values[i] = readInt();
		}
		return values;
	}

	public long readLong() throws IOException {
		return (((long) readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
	}

	public long readVLong() throws IOException {
		byte b = readByte();
		long i = b & 0x7FL;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 7;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 14;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 21;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 28;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 35;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 42;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		i |= (b & 0x7FL) << 49;
		if ((b & 0x80) == 0) {
			return i;
		}
		b = readByte();
		assert (b & 0x80) == 0;
		return i | ((b & 0x7FL) << 56);
	}

	public long[] readLongArray() throws IOException {
		int length = readVInt();
		long[] values = new long[length];
		for (int i = 0; i < length; i++) {
			values[i] = readLong();
		}
		return values;
	}

	public final float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	public float[] readFloatArray() throws IOException {
		int length = readVInt();
		float[] values = new float[length];
		for (int i = 0; i < length; i++) {
			values[i] = readFloat();
		}
		return values;
	}

	public final double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	public double[] readDoubleArray() throws IOException {
		int length = readVInt();
		double[] values = new double[length];
		for (int i = 0; i < length; i++) {
			values[i] = readDouble();
		}
		return values;
	}

	public final boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	public final Boolean readOptionalBoolean() throws IOException {
		byte val = readByte();
		if (val == 2) {
			return null;
		}
		if (val == 1) {
			return true;
		}
		return false;
	}

	public String readString() throws IOException {
		final int charCount = readVInt();
		char[] buffered = new char[charCount];
		int c = 0;
		int l = 0;
		while (l < charCount) {
			c = readByte() & 0xff;
			switch (c >> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				buffered[l++] = (char) c;
				break;
			case 12:
			case 13:
				buffered[l++] = (char) ((c & 0x1F) << 6 | readByte() & 0x3F);
				break;
			case 14:
				buffered[l++] = (char) ((c & 0x0F) << 12 | (readByte() & 0x3F) << 6 | (readByte() & 0x3F) << 0);
				break;
			}
		}
		return new String(buffered, 0, charCount);
	}

	@SuppressWarnings("unchecked")
	public Map<String, ?> readMap() throws IOException {
		return (Map<String, ?>) readGenericValue();
	}

	public Object readGenericValue() throws IOException {
		byte type = readByte();
		switch (type) {
		case -1:
			return null;
		case 0:
			return readString();
		case 1:
			return readInt();
		case 2:
			return readLong();
		case 3:
			return readFloat();
		case 4:
			return readDouble();
		case 5:
			return readBoolean();
		case 6:
			return readByte();
		case 7:
			return readShort();
		case 8:
			return new Date(readLong());
		case 10:
			return readIntArray();
		case 11:
			return readLongArray();
		case 12:
			return readFloatArray();
		case 13:
			return readDoubleArray();
		case 14: {
			int len = readVInt();
			Object[] objs = new Object[len];
			for (int i = 0; i < len; i++) {
				objs[i] = readGenericValue();
			}
			return objs;
		}
		case 21: {
			int size = readVInt();
			List<Object> list = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				list.add(readGenericValue());
			}
			return list;
		}
		case 22: {
			int size = readVInt();
			Set<Object> set = new HashSet<Object>();
			for (int i = 0; i < size; i++) {
				set.add(readGenericValue());
			}
			return set;
		}
		case 23: {
			int size = readVInt();
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < size; i++) {
				map.put(readString(), readGenericValue());
			}
			return map;
		}
		case 24: {
			int size = readVInt();
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < size; i++) {
				map.put(readString(), readGenericValue());
			}
			return map;
		}
		default:
			throw new IOException("Can't read unknown type [" + type + "]");
		}
	}

}
