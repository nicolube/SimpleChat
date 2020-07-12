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

import de.nicolube.simplechat.common.PacketDecoder;
import de.nicolube.simplechat.common.PacketEncoder;
import de.nicolube.simplechat.common.User;
import de.nicolube.simplechat.packets.ChatInPacket;
import de.nicolube.simplechat.packets.ChatOutPacket;
import de.nicolube.simplechat.packets.UserListPacket;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author nicolue.de
 */
public class Server {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    private final Map<Channel, User> users;
    private final LinkedList<CachedMessage> cachedMessages;

    public Server() throws InterruptedException, SSLException {
        this.config = new Config();
        this.users = new HashMap();
        this.cachedMessages = new LinkedList<>();
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
                            users.put(ch, new User(ch));
                        }
                    })
                    .bind(config.getHost(), config.getPort()).sync().channel().closeFuture().syncUninterruptibly();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException, SSLException {
        new Server();

    }

    public void receiveMessage(User user, ChatInPacket packet) {
        String message = packet.getMessage();
        ChatOutPacket chatOutPacket = new ChatOutPacket(user.getUsername(), message);
        this.cachedMessages.addLast(new CachedMessage(user, message));
        this.users.forEach((ch, u) -> u.getChannel().writeAndFlush(chatOutPacket));
    }

    public void login(String user, Channel channel) {
        getUserByChannel(channel).setUsername(user);
        this.cachedMessages.forEach((cm) -> channel.writeAndFlush(new ChatOutPacket(cm.getUser().getUsername(), cm.getMessage())));
        updateUserList();
    }

    public void logout(Channel channel) {
        this.users.remove(channel);
        updateUserList();
    }

    private void updateUserList() {
        UserListPacket userListPacket = new UserListPacket(this.users.values().stream().map(User::getUsername).toArray(String[]::new));
        this.users.values().forEach(u -> u.getChannel().writeAndFlush(userListPacket));
    }

    public User getUserByChannel(Channel channel) {
        return this.users.get(channel);
    }

}
