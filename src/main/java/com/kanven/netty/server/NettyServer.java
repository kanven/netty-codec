package com.kanven.netty.server;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import org.apache.commons.lang3.StringUtils;

import com.kanven.netty.codec.NettyDecoder;
import com.kanven.netty.codec.NettyEncoder;
import com.kanven.netty.config.ProtocolConfig;
import com.kanven.netty.utils.ThreadFactoryImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

/**
 * 服务端实现
 * 
 * @author kanven
 *
 */
public class NettyServer {

	private static final int STATUS_INIT = 0; // 初始化

	private static final int STATUS_STARTED = 1; // 启动完成

	private static final int STATUS_SHUTDOWN = -1; // 关闭

	private ServerBootstrap bootstrap = new ServerBootstrap();

	private MultithreadEventLoopGroup boss;

	private MultithreadEventLoopGroup worker;

	private ProtocolConfig config;

	private ChannelInboundHandlerAdapter handler;

	@SuppressWarnings("unused")
	private volatile int status = STATUS_INIT;

	private static final AtomicIntegerFieldUpdater<NettyServer> STATUS_UPDATER;

	static {// 无锁设计
		STATUS_UPDATER = AtomicIntegerFieldUpdater.newUpdater(NettyServer.class, "status");
	}

	public NettyServer(ProtocolConfig config, ChannelInboundHandlerAdapter handler) {
		this.config = config;
		this.handler = handler;
		init();
	}

	private void init() {
		if (Epoll.isAvailable()) {
			boss = new EpollEventLoopGroup(config.getAccepts(),
					new ThreadFactoryImpl(config.getName() + "_acceptor", true));
			worker = new EpollEventLoopGroup(config.getAccepts(),
					new ThreadFactoryImpl(config.getName() + "_worker", true));
			bootstrap.childHandler(new ChildHandlerInitializer<EpollSocketChannel>(handler));
		} else {
			boss = new NioEventLoopGroup(config.getAccepts(),
					new ThreadFactoryImpl(config.getName() + "_acceptor", true));
			worker = new NioEventLoopGroup(config.getAccepts(),
					new ThreadFactoryImpl(config.getName() + "_worker", true));
			bootstrap.childHandler(new ChildHandlerInitializer<SocketChannel>(handler));
		}
		bootstrap.group(boss, worker);
		bootstrap.option(ChannelOption.SO_BACKLOG, config.getBlockLog())
				.option(ChannelOption.TCP_NODELAY, config.isNoDelay())
				.option(ChannelOption.SO_REUSEADDR, config.isReUseAddr())
				.childOption(ChannelOption.SO_KEEPALIVE, config.isKeepalive());
	}

	public void start() {
		switch (STATUS_UPDATER.get(this)) {
		case STATUS_INIT: {
			if (STATUS_UPDATER.compareAndSet(this, STATUS_INIT, STATUS_STARTED)) {
				ChannelFuture future = StringUtils.isNoneBlank(config.getHost())
						? bootstrap.bind(config.getHost(), config.getPort()) : bootstrap.bind(config.getPort());
				try {
					future.sync();
				} catch (InterruptedException e) {
					STATUS_UPDATER.compareAndSet(this, STATUS_STARTED, STATUS_SHUTDOWN);
					throw new IllegalStateException("服务启动失败！", e);
				}
			}
			break;
		}
		case STATUS_SHUTDOWN:
			throw new IllegalStateException("服务被关闭，无法启动！");
		}
	}

	public void stop() {
		if (STATUS_UPDATER.compareAndSet(this, STATUS_STARTED, STATUS_SHUTDOWN)) {
			if (boss != null) {
				try {
					boss.shutdownGracefully();
				} finally {
					if (worker != null) {
						worker.shutdownGracefully();
					}
				}
			}
		}
	}

	private static class ChildHandlerInitializer<T extends Channel> extends ChannelInitializer<T> {

		private ChannelInboundHandlerAdapter handler;

		public ChildHandlerInitializer(ChannelInboundHandlerAdapter handler) {
			this.handler = handler;
		}

		@Override
		protected void initChannel(T ch) throws Exception {
			ChannelPipeline pipeline = ch.pipeline();
			pipeline.addLast(new NettyEncoder());
			pipeline.addLast(new NettyDecoder());
			pipeline.addLast(handler);
		}

	}

}
