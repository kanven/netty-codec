package com.kanven.netty;

public class Constants {

	public static final short PROTOCOL_MAGIC = (short) 0xF5F2;

	public static final byte PROTOCOL_VERSION = 0x01;

	public static final byte PROTOCOL_BODY_VERSION = 0x01;

	public static final int PROTOCOL_HEADER_LENGTH = 8;

	public static final byte PROCOTOL_REQ_FLAG = 0x00;

	public static final byte PROTOCOL_RESP_FLAG = 0x01;

	public static final byte PROTOCOL_OTHER = (byte) 0xFF;

}
