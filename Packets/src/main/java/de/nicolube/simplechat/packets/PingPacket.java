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

/**
 *
 * @author nicolue.de
 */
public class PingPacket extends Packet {

    private long ping;

    public PingPacket(long ping) {
        this.ping = ping;
    }

    public PingPacket() {
    }

    @Override
    public void read(ByteBuf byteBuf) {
        this.ping = byteBuf.readLong();
    }

    @Override
    public void write(ByteBuf byteBuf) {
        byteBuf.writeLong(this.ping);
    }

    public long getPing() {
        return System.currentTimeMillis()-ping;
    }

}
