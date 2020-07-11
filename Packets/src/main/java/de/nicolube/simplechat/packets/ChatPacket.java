package de.nicolube.simplechat.packets;

import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class ChatPacket extends Packet {

    private String sender;
    private String message;

    public ChatPacket(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public ChatPacket() {
    }

    @Override
    public void read(ByteBuf byteBuf) {
        autoRead(this, byteBuf);
    }

    @Override
    public void write(ByteBuf byteBuf) {
        autoWrite(this, byteBuf);
    }
}
