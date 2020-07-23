package de.nicolube.simplechat.packets;

import de.nicolube.simplechat.common.User;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.lang.reflect.Field;

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
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            this.sender = (User) inputStream.readObject();
            this.message = (String) inputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void write(ByteBuf byteBuf) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(stream);
            outputStream.writeObject(this.sender);
            outputStream.writeObject(this.message);
            byteBuf.writeBytes(stream.toByteArray());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
