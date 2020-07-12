package de.nicolube.simplechat.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class CachedMessage implements Serializable {
    private User user;
    private String message;

    public CachedMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }
}
