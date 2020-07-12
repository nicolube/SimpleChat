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

import de.nicolube.simplechat.client.Client;
import de.nicolube.simplechat.common.CachedMessage;
import de.nicolube.simplechat.common.User;
import de.nicolube.simplechat.packets.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.LinkedList;

/**
 * @author nicolue.de
 */
public class NetworkHandler extends SimpleChannelInboundHandler<Packet> {

    private final Client client;

    public NetworkHandler(Client client) {
        this.client = client;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) throws Exception {
        System.out.println("Receive " + packet.getClass().getSimpleName());
        if (packet instanceof PingPacket) {
            System.out.println("Ping: " + (((PingPacket) packet).getPing()));
            return;
        }
        if (packet instanceof PacketOutChat) {
            PacketOutChat packetOutChat = (PacketOutChat) packet;
            this.client.receiveMessage(packetOutChat.getSender(), packetOutChat.getMessage());
            return;
        }
        if (packet instanceof PacketOutUserList) {
            this.client.updateUserList(((PacketOutUserList) packet).getUsers());
            return;
        }
        if (packet instanceof PacketOutLogin) {
            User user = ((PacketOutLogin) packet).getUser();
            user.setChannel(ctx.channel());
            this.client.login(user);
            return;
        }
        if (packet instanceof PacketOutCachedChat) {
            LinkedList<CachedMessage> messages = ((PacketOutCachedChat) packet).getMessages();
            this.client.receiveCachedChat(messages);
            return;
        }
    }
}
