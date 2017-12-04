package com.kanven.netty.codec;

import java.util.ArrayList;
import java.util.List;

import com.kanven.netty.Req;
import com.kanven.netty.Resp;
import com.kanven.netty.entites.Result;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

	public static void main(String[] args) throws InterruptedException {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_BACKLOG, 1 << 10).option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast(new NettyEncoder());
				pipeline.addLast(new NettyDecoder());
				pipeline.addLast(new ChannelInboundHandlerAdapter() {

					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						if (msg instanceof Req) {
							System.out.println(msg + "======");
							Req req = (Req) msg;
							Resp resp = new Resp();
							resp.setSeq(req.getSeq());
							Result<List<String>> result = new Result<List<String>>();
							result.setStatus(200);
							result.setMessage("请求处理成功！");
							List<String> dts = new ArrayList<String>();
							dts.add("111");
							dts.add("222");
							result.setData(dts);
							resp.setBody(result);
							ctx.writeAndFlush(resp);
						}
					}

				});
			}
		});
		bootstrap.bind(9090).sync();
	}

}
