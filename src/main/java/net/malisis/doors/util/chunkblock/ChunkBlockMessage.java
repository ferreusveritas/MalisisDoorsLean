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

package net.malisis.doors.util.chunkblock;

import java.util.Set;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.network.IMalisisMessageHandler;
import net.malisis.doors.registry.AutoLoad;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author Ordinastie
 *
 */
@AutoLoad(true)
public class ChunkBlockMessage implements IMalisisMessageHandler<ChunkBlockMessage.Packet, IMessage>
{
	public ChunkBlockMessage()
	{
		MalisisDoors.network.registerMessage(this, Packet.class, Side.CLIENT);
	}

	@Override
	public void process(Packet message, MessageContext ctx)
	{
		ChunkBlockHandler.get().setCoords(message.x, message.z, message.coords);
	}

	public static void sendCoords(Chunk chunk, Set<BlockPos> coords, EntityPlayerMP player)
	{
		MalisisDoors.network.sendTo(new Packet(chunk, coords), player);
	}

	public static class Packet implements IMessage
	{
		private int x;
		private int z;
		private Set<BlockPos> coords;

		public Packet()
		{}

		public Packet(Chunk chunk, Set<BlockPos> coords)
		{
			this.x = chunk.x;
			this.z = chunk.z;
			this.coords = coords;
		}

		@Override
		public void fromBytes(ByteBuf buf)
		{
			x = buf.readInt();
			z = buf.readInt();
			coords = Sets.newHashSet();
			int count = buf.readInt();
			for (int i = 0; i < count; i++)
				coords.add(BlockPos.fromLong(buf.readLong()));
		}

		@Override
		public void toBytes(ByteBuf buf)
		{
			buf.writeInt(x);
			buf.writeInt(z);
			buf.writeInt(coords.size());
			coords.forEach(p -> buf.writeLong(p.toLong()));
		}
	}
}
