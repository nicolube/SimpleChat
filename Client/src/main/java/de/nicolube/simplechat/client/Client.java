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
public class Client extends ClientAPI {

    public static boolean EPPLL = Epoll.isAvailable();

    @Getter
    private final MainFrame mainFrame;
    private User user;

    public Client() {
        super();
        this.mainFrame = new MainFrame(this);
        this.mainFrame.loginPanel();
        ResourceLoader resourceLoader = new ResourceLoader(this.mainFrame.getLoginPanel().getLoadingBar());
        resourceLoader.add(() -> Sounds.cacheAudio("/sounds/notification.wav"));
        resourceLoader.run().thenRun(() -> this.mainFrame.getLoginPanel().getLoginButton().setEnabled(true));
        Runtime.getRuntime().addShutdownHook(new Thread(this::logout));

    }

    public static void main(String[] args) {
        new Client();
    }

    @Override
    public void receiveMessage(User sender, String message) {
        ReceiveType type = ReceiveType.OTHER;
        if (this.user.getUuid().equals(sender.getUuid())) type = ReceiveType.SELF;
        this.mainFrame.getMainPanel().addMessage(sender, message, type);
    }

    @Override
    public void receiveCachedChat(LinkedList<CachedMessage> messages) {
        for (CachedMessage cm : messages) {
            User sender = cm.getUser();
            ReceiveType type = ReceiveType.OTHER;
            if (this.user.getUuid().equals(sender.getUuid())) type = ReceiveType.SELF;
            this.mainFrame.getMainPanel().addMessage(cm, type);
        }
    }

    @Override
    public void updateUserList(String[] userList) {
        StringBuilder sb = new StringBuilder();
        for (String user : userList) {
            sb.append(user).append("\n");
        }
        getMainFrame().getMainPanel().getUserListPane().setText(sb.toString());
    }

    @Override
    public void login(User user) {
        this.mainFrame.mainPanel();
        this.user = user;
    }

}
