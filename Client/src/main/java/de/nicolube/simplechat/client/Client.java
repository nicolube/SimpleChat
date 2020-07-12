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

import de.nicolube.simplechat.client.handlers.NetworkHandler;
import de.nicolube.simplechat.common.CachedMessage;
import de.nicolube.simplechat.common.PacketDecoder;
import de.nicolube.simplechat.common.PacketEncoder;
import de.nicolube.simplechat.common.User;
import de.nicolube.simplechat.packets.PacketInChat;
import de.nicolube.simplechat.packets.PacketInLogin;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

/**
 * @author nicolue.de
 */
public class Client {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    @Getter
    private final MainFrame mainFrame;
    private MultithreadEventLoopGroup eventLoopGroup;
    private Channel channel;
    private User user;

    public Client() {
        this.config = new Config();
        this.mainFrame = new MainFrame(this);
        this.mainFrame.loginPanel();
        ResourceLoader resourceLoader = new ResourceLoader(this.mainFrame.getLoginPanel().getLoadingBar());
        resourceLoader.add(() -> Sounds.cacheAudio("/sounds/notification.wav"));
        resourceLoader.run().thenRun(() -> this.mainFrame.getLoginPanel().getLoginButton().setEnabled(true));
        Runtime.getRuntime().addShutdownHook(new Thread(this::logout));

    }

    @SneakyThrows
    public void initChannel() {
        Client client = this;
        this.eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
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

    public static void main(String[] args) {
        new Client();
    }

    public void sendMessage(String message) {
        PacketInChat packetInChat = new PacketInChat(message);
        this.channel.pipeline().writeAndFlush(packetInChat);
    }

    public void receiveMessage(User sender, String message) {
        ReceiveType type = ReceiveType.OTHER;
        if (this.user.getUuid().equals(sender.getUuid())) type = ReceiveType.SELF;
        this.mainFrame.getMainPanel().addMessage(sender, message, type);
    }

    public void receiveCachedChat(LinkedList<CachedMessage> messages) {
        for (CachedMessage cm : messages) {
            User sender = cm.getUser();
            ReceiveType type = ReceiveType.OTHER;
            if (this.user.getUuid().equals(sender.getUuid())) type = ReceiveType.SELF;
            this.mainFrame.getMainPanel().addMessage(cm, type);
        }
    }

    public void updateUserList(String[] userList) {
        StringBuilder sb = new StringBuilder();
        for (String user : userList) {
            sb.append(user).append("\n");
        }
        getMainFrame().getMainPanel().getUserListPane().setText(sb.toString());
    }

    public void prepareLogin(String username) {
        this.channel.writeAndFlush(new PacketInLogin(username));
    }

    public void login(User user) {
        System.out.println("login");
        this.mainFrame.mainPanel();
        this.user = user;
    }

    public void logout() {
        if (this.channel.isOpen()) {
            System.out.println("logout");
            this.channel.disconnect();
            this.channel.close();
            this.eventLoopGroup.shutdownGracefully();
        }
    }

}
