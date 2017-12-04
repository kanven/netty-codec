package com.kanven.netty.codec.stream.in;

import java.io.EOFException;
import java.io.IOException;

import com.kanven.netty.codec.stream.StreamInput;

public class BytesStreamInput extends StreamInput {

	private final byte[] buffer;

	private int start;

	private int pos;

	private int end;

	public BytesStreamInput(byte[] buffer) {
		this(buffer, 0, buffer.length);
	}

	public BytesStreamInput(byte[] buffer, int offset, int length) {
		this.buffer = buffer;
		this.start = offset;
		this.pos = offset;
		this.end = start + length;
		if (end > buffer.length) {
			end = buffer.length;
		}
	}

	@Override
	public byte readByte() throws IOException {
		if (pos >= end) {
			throw new EOFException();
		}
		return buffer[pos++];
	}

	@Override
	public void readBytes(byte[] b, int offset, int len) throws IOException {
		if (len == 0) {
			return;
		}
		if (len < 0 || pos >= end) {
			throw new EOFException();
		}
		if (pos + len > end) {
			len = end - pos;
		}
		System.arraycopy(buffer, pos, b, offset, len);
		pos += len;
	}

	@Override
	public int read() throws IOException {
		return pos < end ? buffer[pos++] & 0xFF : -1;
	}

	@Override
	public long skip(long n) throws IOException {
		if (n < 0) {
			return 0;
		}
		if (pos + n > end) {
			n = end - pos;
		}
		pos += n;
		return pos;
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public synchronized void reset() throws IOException {
		pos = start;
	}

}
