/* 
 * Copyright (C) 2020 nicolube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.nicolube.simplechat.packets;

import io.netty.buffer.ByteBuf;

import java.io.*;
import java.lang.reflect.Field;

/**
 * @author nicolue.de
 */
public abstract class Packet {

    public abstract void read(ByteBuf byteBuf);

    public abstract void write(ByteBuf byteBuf);

    public void autoRead(Packet packet, ByteBuf byteBuf) {
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            ObjectInputStream inputStream = new ObjectInputStream(stream);
            for (Field field : packet.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(packet, inputStream.readObject());
            }
        } catch (IOException | IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }


    public void autoWrite(Packet packet, ByteBuf byteBuf) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(stream);
            for (Field field : packet.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                outputStream.writeObject(field.get(packet));
            }
            byteBuf.writeBytes(stream.toByteArray());
        } catch (IOException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

}
