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

package net.malisis.doors.tileentity;

import net.malisis.doors.network.SwapperMessage;
import net.malisis.doors.block.component.DirectionalComponent;
import net.malisis.doors.util.BlockPosUtils;
import net.malisis.doors.util.EntityUtils;
import net.malisis.doors.util.ItemUtils;
import net.malisis.doors.util.MBlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Ordinastie
 *
 */
public class SwapperTileEntity extends TileEntity
{
	private IBlockState[][][] states;
	private NBTTagCompound[][][] tileEntities;

	public SwapperTileEntity()
	{
		states = new IBlockState[3][3][3];
		tileEntities = new NBTTagCompound[3][3][3];
	}

	public void swap()
	{
		AxisAlignedBB aabb = getAABB();
		//Set<Chunk> chunks = Sets.newHashSet();
		for (BlockPos p : BlockPosUtils.getAllInBox(aabb))
		{
			int x = (int) (p.getX() - aabb.minX);
			int y = (int) (p.getY() - aabb.minY);
			int z = (int) (p.getZ() - aabb.minZ);

			Pair<IBlockState, NBTTagCompound> worldState = getWorldState(p);
			Pair<IBlockState, NBTTagCompound> storedState = getStoredState(x, y, z);

			storeState(x, y, z, worldState);
			applyState(p, storedState);

			IBlockState stored = storedState.getLeft();
			if (stored == null)
				stored = Blocks.AIR.getDefaultState();
			getWorld().markAndNotifyBlock(p, getWorld().getChunkFromBlockCoords(p), worldState.getLeft(), stored, 3);
			//			if (storedState.getRight() != null)
			//				chunks.add(getWorld().getChunkFromBlockCoords(p));
		}
	}

	private AxisAlignedBB getAABB()
	{
		EnumFacing direction = DirectionalComponent.getDirection(getWorld(), getPos());
		AxisAlignedBB aabb = new AxisAlignedBB(getPos().add(-1, -1, -1), getPos().add(2, 2, 2));
		aabb = aabb.offset(direction.getFrontOffsetX() * 2, direction.getFrontOffsetY() * 2, direction.getFrontOffsetZ() * 2);
		return aabb;
	}

	private Pair<IBlockState, NBTTagCompound> getWorldState(BlockPos pos)
	{
		IBlockState worldState = getWorld().getBlockState(pos);

		if (worldState.getBlock() == Blocks.BEDROCK)
			return Pair.of(null, null);

		TileEntity te = getWorld().getTileEntity(pos);
		if (te == null)
			return Pair.of(worldState, null);
		NBTTagCompound nbt = new NBTTagCompound();
		te.writeToNBT(nbt);
		return Pair.of(worldState, nbt);
	}

	private Pair<IBlockState, NBTTagCompound> getStoredState(int x, int y, int z)
	{
		return Pair.of(states[x][y][z], tileEntities[x][y][z]);
	}

	private void applyState(BlockPos pos, Pair<IBlockState, NBTTagCompound> state)
	{
		if (getWorld().getBlockState(pos).getBlock() == Blocks.BEDROCK)
			return;

		clearWorldState(pos);
		getWorld().setBlockState(pos, state.getLeft() != null ? state.getLeft() : Blocks.AIR.getDefaultState(), 3);
		TileEntity te = getWorld().getTileEntity(pos);
		if (te != null && state.getRight() != null)
		{
			te.readFromNBT(state.getRight());
			SwapperMessage.sendTileEntityTag(te);
		}
	}

	private void storeState(int x, int y, int z, Pair<IBlockState, NBTTagCompound> state)
	{
		states[x][y][z] = state.getLeft();
		tileEntities[x][y][z] = state.getRight();
	}

	private void clearWorldState(BlockPos pos)
	{
		ExtendedBlockStorage ebs = getWorld().getChunkFromBlockCoords(pos).getBlockStorageArray()[pos.getY() >> 4];
		ebs.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, Blocks.AIR.getDefaultState());
		getWorld().removeTileEntity(pos);
	}

	public void dropStoredStates()
	{
		for (int x = 0; x <= 2; x++)
			for (int y = 0; y <= 2; y++)
				for (int z = 0; z <= 2; z++)
					EntityUtils.spawnEjectedItem(getWorld(), getPos(), ItemUtils.getItemStackFromState(states[x][y][z]));
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		for (int x = 0; x <= 2; x++)
		{
			for (int y = 0; y <= 2; y++)
			{
				for (int z = 0; z <= 2; z++)
				{
					int index = x + y * 3 + z * 9;
					states[x][y][z] = MBlockState.fromNBT(tag, "block_" + index, "metadata_" + index);
					if (tag.hasKey("tileEntity_" + index))
						tileEntities[x][y][z] = tag.getCompoundTag("tileEntity_" + index);
				}
			}
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		for (int x = 0; x <= 2; x++)
		{
			for (int y = 0; y <= 2; y++)
			{
				for (int z = 0; z <= 2; z++)
				{
					int index = x + y * 3 + z * 9;
					Pair<IBlockState, NBTTagCompound> storedState = getStoredState(x, y, z);
					IBlockState state = storedState.getLeft();
					if (state != null && state.getBlock() != Blocks.AIR)
					{
						MBlockState.toNBT(tag, state, "block_" + index, "metadata_" + index);
						if (storedState.getRight() != null)
							tag.setTag("tileEntity_" + index, storedState.getRight());
					}
				}
			}
		}

		return tag;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
	{
		this.readFromNBT(packet.getNbtCompound());
		AxisAlignedBB aabb = getAABB();
		//force rerender of the block on the client
		getWorld().markBlockRangeForRenderUpdate(new BlockPos(aabb.minX, aabb.minY, aabb.minZ),
				new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ));
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}

}
