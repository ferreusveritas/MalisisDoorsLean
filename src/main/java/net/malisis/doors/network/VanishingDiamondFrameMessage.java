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

package net.malisis.doors.network;

import io.netty.buffer.ByteBuf;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.MalisisDoors.Items;
import net.malisis.doors.tileentity.VanishingDiamondTileEntity;
import net.malisis.doors.vanishingoption.VanishingOptions;
import net.malisis.doors.network.IMalisisMessageHandler;
import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.util.EntityUtils;
import net.malisis.doors.util.TileEntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@AutoLoad(true)
public class VanishingDiamondFrameMessage implements IMalisisMessageHandler<VanishingDiamondFrameMessage.Packet, IMessage>
{
	public static enum DataType
	{
		PROPAGATION,
		DELAY,
		INVERSED,
		DURATION;
	}

	public VanishingDiamondFrameMessage()
	{
		MalisisDoors.network.registerMessage(this, VanishingDiamondFrameMessage.Packet.class, Side.SERVER);
	}

	@Override
	public void process(Packet message, MessageContext ctx)
	{
		VanishingOptions vanishingOptions = null;
		EntityPlayer player = IMalisisMessageHandler.getPlayer(ctx);
		if (message.isTileEntity)
		{

			VanishingDiamondTileEntity te = TileEntityUtils.getTileEntity(VanishingDiamondTileEntity.class, player.world, message.pos);
			if (te == null)
				return;
			vanishingOptions = te.getVanishingOptions();
			TileEntityUtils.notifyUpdate(te);
		}
		else
		{
			if (!EntityUtils.isEquipped(player, Items.vanishingCopierItem, EnumHand.MAIN_HAND))
				return;

			vanishingOptions = Items.vanishingCopierItem.getVanishingOptions(player.getHeldItem(EnumHand.MAIN_HAND));
		}

		if (vanishingOptions == null)
			return;

		vanishingOptions.set(message.facing, message.type, message.time, message.checked);

		if (!message.isTileEntity)
			vanishingOptions.save();
	}

	public static void sendConfiguration(VanishingDiamondTileEntity te, EnumFacing facing, DataType type, int time, boolean checked)
	{
		Packet packet = null;
		if (te != null)
			packet = new Packet(te.getPos(), type, facing, time, checked);
		else
			packet = new Packet(type, facing, time, checked);
		MalisisDoors.network.sendToServer(packet);
	}

	public static void sendConfiguration(EnumFacing facing, DataType type, int time, boolean checked)
	{
		Packet packet = new Packet(type, facing, time, checked);
		MalisisDoors.network.sendToServer(packet);
	}

	public static class Packet implements IMessage
	{
		protected boolean isTileEntity;
		protected BlockPos pos;
		protected DataType type;
		protected EnumFacing facing;
		protected int time;
		protected boolean checked;

		public Packet()
		{}

		public Packet(DataType type, EnumFacing facing, int time, boolean checked)
		{
			this.type = type;
			this.facing = facing;
			this.time = time;
			this.checked = checked;
		}

		public Packet(BlockPos pos, DataType type, EnumFacing facing, int time, boolean checked)
		{
			this(type, facing, time, checked);
			this.pos = pos;
			this.isTileEntity = true;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			isTileEntity = buf.readBoolean();
			if (isTileEntity)
				pos = BlockPos.fromLong(buf.readLong());
			type = DataType.values()[buf.readByte()];
			if (type != DataType.DURATION)
				facing = EnumFacing.values()[buf.readByte()];
			if (type == DataType.PROPAGATION || type == DataType.INVERSED)
				checked = buf.readBoolean();
			else
				time = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeBoolean(isTileEntity);
			if (isTileEntity)
				buf.writeLong(pos.toLong());
			buf.writeByte(type.ordinal());
			if (type != DataType.DURATION)
				buf.writeByte(facing.ordinal());
			if (type == DataType.PROPAGATION || type == DataType.INVERSED)
				buf.writeBoolean(checked);
			else
				buf.writeInt(time);
		}
	}

}
