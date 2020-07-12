package de.nicolube.simplechat.common;


import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class User implements Serializable {
    private String username;
    private UUID uuid;
    @Setter
    private transient Channel channel;

    public User(String username, UUID uuid, Channel channel) {
        this.username = username;
        this.uuid = uuid;
        this.channel = channel;
    }
}
