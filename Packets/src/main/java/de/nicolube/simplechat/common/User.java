package de.nicolube.simplechat.common;


import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class User {
    @Setter
    private String username;
    private transient Channel channel;

    public User(Channel channel) {
        this.channel = channel;
    }
}
