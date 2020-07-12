package de.nicolube.simplechat.packets;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatOutPacket extends Packet {

    private String sender;
    private String message;

    public ChatOutPacket(String sender, String message) {
        this.sender = sender;
        this.message = message;
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
