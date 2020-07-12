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
package de.nicolube.simplechat.common;

import de.nicolube.simplechat.packets.Packet;
import de.nicolube.simplechat.packets.PacketRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 *
 * @author nicolue.de
 */
public class PacketEncoder extends MessageToByteEncoder<Packet> {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        int id = PacketRegistry.getPacketID(packet.getClass());
        System.out.println("Send Packet" + packet.getClass().getSimpleName() + " with id: "+id);
        if (id < 0) throw new IllegalArgumentException("The Packet no valid packet ID.");
        out.writeInt(id);
        packet.write(out);
    }
    
}
