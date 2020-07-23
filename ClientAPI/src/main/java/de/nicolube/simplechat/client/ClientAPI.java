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
import io.netty.channel.ChannelOption;
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

public abstract class ClientAPI {

    public static boolean EPPLL = Epoll.isAvailable();

    private final Config config;
    @Getter
    private MultithreadEventLoopGroup eventLoopGroup;
    private Channel channel;

    public ClientAPI() {
        this.config = new Config();
        Runtime.getRuntime().addShutdownHook(new Thread(this::logout));

    }

    @SneakyThrows
    public void initChannel() {
        this.eventLoopGroup = EPPLL ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        CompletableFuture.runAsync(() -> {
            try {
                SslContext sslContext = SslContextBuilder.forClient().trustManager(getClass().getResourceAsStream("/csr.pem")).build();
                this.channel = new Bootstrap()
                        .group(eventLoopGroup)
                        .channel(EPPLL ? EpollSocketChannel.class : NioSocketChannel.class)
                        .option(ChannelOption.SO_KEEPALIVE, true)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        .handler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline()
                                        .addLast("ssl", sslContext.newHandler(ch.alloc()))
                                        .addLast(new PacketDecoder())
                                        .addLast(new PacketEncoder())
                                        .addLast(new NetworkHandler(ClientAPI.this));
                            }
                        })
                        .connect(config.getHost(), config.getPort()).sync().channel();
                this.channel.closeFuture().syncUninterruptibly();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                eventLoopGroup.shutdownGracefully();
            }
        });
    }


    public void sendMessage(String message) {
        PacketInChat packetInChat = new PacketInChat(message);
        this.channel.pipeline().writeAndFlush(packetInChat);
    }

    public abstract void receiveMessage(User sender, String message);

    public abstract void receiveCachedChat(LinkedList<CachedMessage> messages);

    public abstract void updateUserList(String[] userList);

    public void prepareLogin(String username) {
        this.channel.writeAndFlush(new PacketInLogin(username));
    }

    public abstract void login(User user);

    public void logout() {
        if (this.channel.isOpen()) {
            System.out.println("logout");
            this.channel.disconnect();
            this.channel.close();
            this.eventLoopGroup.shutdownGracefully();
        }
    }
}
