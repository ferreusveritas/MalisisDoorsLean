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
import net.malisis.doors.inventory.MalisisInventoryContainer;
import net.malisis.doors.inventory.MalisisInventoryContainer.ActionType;
import net.malisis.doors.network.IMalisisMessageHandler;
import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.util.Utils;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Message to handle the inventory actions sent from a GUI.
 *
 * @author Ordinastie
 *
 */
@AutoLoad(true)
public class InventoryActionMessage implements IMalisisMessageHandler<InventoryActionMessage.Packet, IMessage>
{
	public InventoryActionMessage()
	{
		MalisisDoors.network.registerMessage(this, Packet.class, Side.SERVER);
	}

	/**
	 * Handles the {@link Packet} received from the client.<br>
	 * Passes the action to the {@link MalisisInventoryContainer}, and send the changes back to the client.
	 *
	 * @param message the message
	 * @param ctx the ctx
	 */
	@Override
	public void process(Packet message, MessageContext ctx)
	{
		Container c = ctx.getServerHandler().player.openContainer;
		if (message.windowId != c.windowId || !(c instanceof MalisisInventoryContainer))
			return;

		MalisisInventoryContainer container = (MalisisInventoryContainer) c;
		container.handleAction(message.action, message.inventoryId, message.slotNumber, message.code);
		container.detectAndSendChanges();
	}

	/**
	 * Sends GUI action to the server {@link MalisisInventoryContainer}.
	 *
	 * @param action the action
	 * @param inventoryId the inventory id
	 * @param slotNumber the slot number
	 * @param code the code
	 */
	@SideOnly(Side.CLIENT)
	public static void sendAction(ActionType action, int inventoryId, int slotNumber, int code)
	{
		int windowId = Utils.getClientPlayer().openContainer.windowId;
		Packet packet = new Packet(action, inventoryId, slotNumber, code, windowId);
		MalisisDoors.network.sendToServer(packet);
	}

	/**
	 * The packet holding the data
	 */
	public static class Packet implements IMessage
	{
		private ActionType action;
		private int inventoryId;
		private int slotNumber;
		private int code;
		private int windowId;

		public Packet()
		{}

		public Packet(ActionType action, int inventoryId, int slotNumber, int code, int windowId)
		{
			this.action = action;
			this.inventoryId = inventoryId;
			this.slotNumber = slotNumber;
			this.code = code;
			this.windowId = windowId;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			action = ActionType.values()[buf.readByte()];
			inventoryId = buf.readInt();
			slotNumber = buf.readInt();
			code = buf.readInt();
			windowId = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeByte(action.ordinal());
			buf.writeInt(inventoryId);
			buf.writeInt(slotNumber);
			buf.writeInt(code);
			buf.writeInt(windowId);
		}
	}

}
