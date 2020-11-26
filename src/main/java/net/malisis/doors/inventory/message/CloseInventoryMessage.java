/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.doors.inventory.message;

import io.netty.buffer.ByteBuf;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.client.gui.MalisisGui;
import net.malisis.doors.inventory.MalisisInventory;
import net.malisis.doors.network.IMalisisMessageHandler;
import net.malisis.doors.registry.AutoLoad;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Message to tell the client to open a GUI.
 *
 * @author Ordinastie
 *
 */
@AutoLoad(true)
public class CloseInventoryMessage implements IMalisisMessageHandler<CloseInventoryMessage.Packet, IMessage>
{
	public CloseInventoryMessage()
	{
		MalisisDoors.network.registerMessage(this, Packet.class, Side.CLIENT);
	}

	/**
	 * Handles the received {@link Packet} on the client. Closes the GUI.
	 *
	 * @param message the message
	 * @param ctx the ctx
	 */
	@Override
	public void process(Packet message, MessageContext ctx)
	{
		if (MalisisGui.currentGui() != null)
			MalisisGui.currentGui().close();
	}

	/**
	 * Sends a packet to client to notify it to open a {@link MalisisInventory}.
	 *
	 * @param player the player
	 */
	public static void send(EntityPlayerMP player)
	{
		Packet packet = new Packet();
		MalisisDoors.network.sendTo(packet, player);
	}

	public static class Packet implements IMessage
	{
		public Packet()
		{}

		@Override
		public void fromBytes(ByteBuf buf)
		{}

		@Override
		public void toBytes(ByteBuf buf)
		{}
	}
}
