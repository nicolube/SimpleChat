package de.nicolube.simplechat.packets;

import de.nicolube.simplechat.common.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketOutChat extends Packet {

    private User sender;
    private String message;

    public PacketOutChat(User sender, String message) {
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
