package de.nicolube.simplechat.server;

import de.nicolube.simplechat.common.User;
import lombok.Data;

@Data
public class CachedMessage {
    private User user;
    private String message;

    public CachedMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
