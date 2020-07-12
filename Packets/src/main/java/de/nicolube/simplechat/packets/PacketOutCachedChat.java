package de.nicolube.simplechat.packets;

import de.nicolube.simplechat.common.CachedMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

@Data
@NoArgsConstructor
public class PacketOutCachedChat extends Packet {

    private LinkedList<CachedMessage> messages;

    public PacketOutCachedChat(LinkedList<CachedMessage> messages) {
        this.messages = messages;
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
