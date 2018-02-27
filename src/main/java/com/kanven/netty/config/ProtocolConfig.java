package com.kanven.netty.config;

public class ProtocolConfig {

	private String name;

	private String host;

	private int port;

	private boolean noDelay = true;

	private boolean keepalive = true;

	private boolean reUseAddr = true;

	private int blockLog = 1 << 10;

	private int accepts = Runtime.getRuntime().availableProcessors() * 2;

	public ProtocolConfig() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNoDelay() {
		return noDelay;
	}

	public void setNoDelay(boolean noDelay) {
		this.noDelay = noDelay;
	}

	public boolean isKeepalive() {
		return keepalive;
	}

	public void setKeepalive(boolean keepalive) {
		this.keepalive = keepalive;
	}

	public boolean isReUseAddr() {
		return reUseAddr;
	}

	public void setReUseAddr(boolean reUseAddr) {
		this.reUseAddr = reUseAddr;
	}

	public int getBlockLog() {
		return blockLog;
	}

	public void setBlockLog(int blockLog) {
		this.blockLog = blockLog;
	}

	public int getAccepts() {
		return accepts;
	}

	public void setAccepts(int accepts) {
		this.accepts = accepts;
	}

	@Override
	public String toString() {
		return "ProtocolConfig [host=" + host + ", port=" + port + ", noDelay=" + noDelay + ", keepalive=" + keepalive
				+ ", reUseAddr=" + reUseAddr + ", blockLog=" + blockLog + ", accepts=" + accepts + "]";
	}

}
