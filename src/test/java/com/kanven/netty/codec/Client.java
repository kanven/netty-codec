package com.kanven.netty.codec;

import java.util.HashMap;
import java.util.Map;

import com.kanven.netty.Req;
import com.kanven.netty.entites.Message;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

	public static void main(String[] args) throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.option(ChannelOption.TCP_NODELAY, true).option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();
				pipeline.addLast(new NettyDecoder());
				pipeline.addLast(new NettyEncoder());
				pipeline.addLast(new ChannelInboundHandlerAdapter() {

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						System.out.println("====resp:" + msg);
					}

				});
			}
		});
		ChannelFuture future = bootstrap.connect("127.0.0.1", 9090).sync();
		Channel channel = future.channel();
		Req req = new Req();
		req.setSeq(1);
		Message message = new Message();
		message.setCid(1);
		message.setContent("我是中国人，我爱中华人民共和国❤️!");
		req.setBody(message);
		Map<String, String> attachments = new HashMap<String, String>();
		attachments.put("x", "1");
		attachments.put("y", "2");
		req.setAttachments(attachments);
		channel.writeAndFlush(req);
	}

}
