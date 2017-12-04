package com.kanven.netty;

import java.util.HashMap;
import java.util.Map;

public class Req extends Protocol {

	private static final long serialVersionUID = -7188264813694387479L;

	private Map<String, String> attachments = new HashMap<String, String>();

	public Map<String, String> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, String> attachments) {
		this.attachments = attachments;
	}

	@Override
	public String toString() {
		return "Req [attachments=" + attachments + ", protocol=" + super.toString() + "]";
	}

}
