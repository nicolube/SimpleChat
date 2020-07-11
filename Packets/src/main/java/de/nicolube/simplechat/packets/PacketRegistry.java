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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicolue.de
 */
public enum PacketRegistry {

    PING(PingPacket.class),
    TimePacket(TimePacket.class),
    ChatPacket(ChatPacket.class);

    private final Class<? extends Packet> packetClass;

    private PacketRegistry(Class<? extends Packet> packetClass) {
        this.packetClass = packetClass;
    }

    public static int getPacketID(Class clazz) {
        String sName = clazz.getName();
        for (PacketRegistry value : values()) {
            if (value.getPacketClass().getName().equals(sName)) {
                return value.ordinal();
            }
        }
        return -1;
    }

    public Class<? extends Packet> getPacketClass() {
        return packetClass;
    }
}
