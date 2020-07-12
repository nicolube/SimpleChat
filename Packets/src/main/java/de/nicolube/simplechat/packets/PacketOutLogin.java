package de.nicolube.simplechat.packets;

import de.nicolube.simplechat.common.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacketOutLogin extends Packet {

    private User user;

    public PacketOutLogin(User user) {
        this.user = user;
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
