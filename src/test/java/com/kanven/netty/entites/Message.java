package com.kanven.netty.entites;

public class Message {

	private int cid;

	private String content;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Message [cid=" + cid + ", content=" + content + "]";
	}

}
