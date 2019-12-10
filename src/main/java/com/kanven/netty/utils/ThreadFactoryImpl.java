package com.kanven.netty.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程创建工具类 <br>
 * (辅助线程池创建，方便问题排查)</br>
 * 
 * @author kanven
 * 
 */
public class ThreadFactoryImpl implements ThreadFactory {

	private String prefix;

	private boolean deamon = false;

	private AtomicInteger number = new AtomicInteger(0);

	private ThreadGroup group;
	
	public ThreadFactoryImpl(String prefix){
		this(prefix, false);
	}

	public ThreadFactoryImpl(String prefix, boolean deamon) {
		this.prefix = prefix;
		this.deamon = deamon;
		SecurityManager sm = System.getSecurityManager();
		group = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, prefix + "_" + number.incrementAndGet(), 0);
		t.setDaemon(deamon);
		return t;
	}

}
