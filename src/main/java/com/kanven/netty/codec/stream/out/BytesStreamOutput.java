package com.kanven.netty.codec.stream.out;

import java.io.IOException;

import com.kanven.netty.codec.stream.StreamOutput;

public class BytesStreamOutput extends StreamOutput {

	private final int PAGE_SIZE_BYTE = 1 << 14;

	private byte[] bytes;

	private int count;

	public BytesStreamOutput() {
		bytes = new byte[PAGE_SIZE_BYTE];
	}

	public BytesStreamOutput(int expertSize) {
		if (expertSize <= 0) {
			expertSize = PAGE_SIZE_BYTE;
		}
		bytes = new byte[expertSize];
	}

	@Override
	public void writeByte(byte b) throws IOException {
		ensureCapacity(count + 1);
		bytes[count] = b;
		count++;
	}

	@Override
	public void writeBytes(byte[] b, int offset, int length) throws IOException {
		if (length == 0) {
			return;
		}
		if (b.length < (offset + length)) {
			throw new IllegalArgumentException(
					"Illegal offset " + offset + "/length " + length + " for byte[] of length " + b.length);
		}
		ensureCapacity(count + length);
		System.arraycopy(b, offset, bytes, count, length);
		count += length;
	}

	@Override
	public void reset() throws IOException {
		count = 0;
	}

	@Override
	public long position() {
		return count;
	}

	private void ensureCapacity(int offset) {
		if (offset <= bytes.length) {
			return;
		}
		int newSize = 0;
		if (offset > PAGE_SIZE_BYTE) {
			newSize = offset + PAGE_SIZE_BYTE >>> 3;
		} else {
			newSize = offset + offset >>> 3;
		}
		byte[] dest = new byte[newSize];
		System.arraycopy(bytes, 0, dest, 0, count);
		bytes = dest;
	}

	public byte[] getBytes() {
		byte[] src = new byte[count];
		System.arraycopy(bytes, 0, src, 0, count);
		return src;
	}

}
