package de.nicolube.simplechat.packets;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketOutUserList extends Packet {

    private String[] users;

    public PacketOutUserList(String[] users) {
        this.users = users;
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