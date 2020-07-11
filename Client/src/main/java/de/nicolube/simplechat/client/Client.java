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
package de.nicolube.simplechat.client;

import de.nicolube.simplechat.packets.ChatPacket;
import de.nicolube.simplechat.client.handlers.NetworkHandler;
import de.nicolube.simplechat.packets.PacketDecoder;
import de.nicolube.simplechat.packets.PacketEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author nicolue.de
 */
public class Client {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    @Getter
    private final MainFrame mainFrame;
    private final MultithreadEventLoopGroup eventLoopGroup;
    private Channel channel;
    private String user;

    public Client() {
        this.config = new Config();
        this.mainFrame = new MainFrame(this);
        this.eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run()
            {
                channel.close();
                eventLoopGroup.shutdownGracefully();
            }
        });
    }

    public static void main(String[] args) {
        new Client();

    }

    public void sendMessage(String message) {
        ChatPacket chatPacket = new ChatPacket(this.user, message);
        this.channel.pipeline().writeAndFlush(chatPacket);
    }

    public void receiveMessage(String sender, String message) {
        JTextPane chatPane = this.mainFrame.getMainPanel().getChatPane();
        chatPane.setText(chatPane.getText() + "\n" + sender + ": " + message);
    }

    @SneakyThrows
    public void login(String user) {
        this.user = user;
        this.mainFrame.main();
        Client client = this;
        SslContext sslContext = SslContextBuilder.forClient().trustManager(getClass().getResourceAsStream("/csr.pem")).build();
        CompletableFuture.runAsync(() -> {
            try {
                this.channel = new Bootstrap()
                        .group(eventLoopGroup)
                        .channel(EPPLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline()
                                        .addLast("ssl", sslContext.newHandler(ch.alloc()))
                                        .addLast(new PacketDecoder())
                                        .addLast(new PacketEncoder())
                                        .addLast(new NetworkHandler(client));
                            }
                        })
                        .connect(config.getHost(), config.getPort()).sync().channel();
                this.channel.closeFuture().syncUninterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        });
    }
}
