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
package de.nicolube.simplechat.client.handlers;

import de.nicolube.simplechat.packets.ChatPacket;
import de.nicolube.simplechat.packets.Packet;
import de.nicolube.simplechat.packets.PingPacket;
import de.nicolube.simplechat.client.Client;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @author nicolue.de
 */
public class NetworkHandler extends SimpleChannelInboundHandler<Packet>{

    private final Client client;

    public NetworkHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        if (packet instanceof PingPacket) {
            System.out.println("Ping: "+(((PingPacket) packet).getPing()));
        }
        if (packet instanceof ChatPacket) {
            ChatPacket chatPacket = (ChatPacket) packet;
            this.client.receiveMessage(chatPacket.getSender(), chatPacket.getMessage());
        }
    }
    
}
