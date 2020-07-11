/*
 * Copyright (C) 2020 nicolube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.nicolube.simplechat.server;

import de.nicolube.simplechat.packets.ChatPacket;
import de.nicolube.simplechat.packets.PacketDecoder;
import de.nicolube.simplechat.packets.PacketEncoder;
import de.nicolube.simplechat.server.handlers.NetworkHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nicolue.de
 */
public class Server {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    private final List<Channel> channels;

    public Server() throws InterruptedException, SSLException {
        this.config = new Config();
        this.channels = new ArrayList<>();
        Server server = this;
        EventLoopGroup eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        SslContext sslContext = SslContextBuilder.forServer(getClass().getResourceAsStream("/csr.pem"), getClass().getResourceAsStream("/privkey.pem")).build();
        try {
            new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(EPPLL ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("ssl", sslContext.newHandler(ch.alloc()))
                                    .addLast(new PacketEncoder())
                                    .addLast(new PacketDecoder())
                                    .addLast(new NetworkHandler(server));
                            channels.add(ch);
                        }
                    })
                    .bind(config.getHost(), config.getPort()).sync().channel().closeFuture().addListener((ChannelFutureListener) channels::remove).syncUninterruptibly();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException, SSLException {
        new Server();

    }

    public void receiveMessage(ChatPacket packet) {
        this.channels.forEach(ch -> ch.writeAndFlush(packet));
    }
}
