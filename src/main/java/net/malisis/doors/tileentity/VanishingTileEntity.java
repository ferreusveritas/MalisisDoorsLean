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

import org.apache.commons.lang3.ArrayUtils;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.ProxyAccess;
import net.malisis.doors.block.VanishingBlock;
import net.malisis.doors.util.EntityUtils;
import net.malisis.doors.util.ItemUtils;
import net.malisis.doors.util.MBlockState;
import net.malisis.doors.util.Silenced;
import net.malisis.doors.util.TileEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VanishingTileEntity extends TileEntity implements ITickable
{
	public final static int maxTransitionTime = 8;
	public final static int maxVibratingTime = 15;

	protected IBlockState copiedState;
	protected TileEntity copiedTileEntity;
	protected VanishingBlock.Type frameType;
	protected boolean powered;
	// animation purpose
	protected int duration = maxTransitionTime;
	protected int transitionTimer;
	protected boolean inTransition;

	private Block[] excludes = new Block[] {	MalisisDoors.Blocks.vanishingBlock,
												Blocks.AIR,
												Blocks.LADDER,
												Blocks.STONE_BUTTON,
												Blocks.WOODEN_BUTTON,
												Blocks.LEVER,
												Blocks.VINE };

	public boolean blockDrawn = true;

	public VanishingTileEntity()
	{
		this.frameType = VanishingBlock.Type.WOOD;
		ProxyAccess.get(getWorld());
	}

	public VanishingTileEntity(VanishingBlock.Type frameType)
	{
		this.frameType = frameType;
	}

	public VanishingBlock.Type getType()
	{
		return frameType;
	}

	public IBlockState getCopiedState()
	{
		return copiedState;
	}

	public TileEntity getCopiedTileEntity()
	{
		return copiedTileEntity;
	}

	public int getDuration()
	{
		return duration;
	}

	public boolean isPowered()
	{
		return powered;
	}

	public boolean isInTransition()
	{
		return inTransition;
	}

	public int getTransitionTimer()
	{
		return transitionTimer;
	}

	public void setBlockState(IBlockState state)
	{
		this.copiedState = state;
	}

	public boolean applyItemStack(ItemStack itemStack, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack is = null;
		if (copiedState != null)
			is = copiedState.getBlock().getPickBlock(copiedState, null, ((World) ProxyAccess.get(world)), pos, player);

		if (!setBlockState(itemStack, player, hand, side, hitX, hitY, hitZ))
			return false;

		EntityUtils.spawnEjectedItem(world, pos, is);

		if (itemStack == null)
			return true;

		if (!player.capabilities.isCreativeMode)
			itemStack.shrink(1);

		((World) ProxyAccess.get(world)).notifyNeighborsOfStateChange(pos, getCopiedState().getBlock(), true);
		return true;
	}

	public boolean setBlockState(ItemStack itemStack, EntityPlayer p, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (itemStack == null)
		{
			copiedState = null;
			copiedTileEntity = null;
			TileEntityUtils.notifyUpdate(this);
			return true;
		}

		IBlockState state = ItemUtils.getStateFromItemStack(itemStack);
		if (state == null || ArrayUtils.contains(excludes, state.getBlock()))
			return false;

		World proxy = (World) ProxyAccess.get(getWorld());
		copiedState = state;
		initCopiedTileEntity();
		Silenced.exec(() -> {
			copiedState = state.getBlock().getStateForPlacement(proxy, pos, side, hitX, hitY, hitZ, itemStack.getMetadata(), p, hand);
			if (p != null)
				copiedState.getBlock().onBlockPlacedBy(proxy, pos, copiedState, p, itemStack);
		});

		TileEntityUtils.notifyUpdate(this);
		return true;
	}

	private void initCopiedTileEntity()
	{
		copiedTileEntity = copiedState.getBlock().createTileEntity(getWorld(), copiedState);
		if (copiedTileEntity != null)
		{
			copiedTileEntity.setWorld((World) ProxyAccess.get(getWorld()));
			copiedTileEntity.setPos(pos);
		}
	}

	public void ejectCopiedState()
	{
		ItemStack is = ItemUtils.getItemStackFromState(getCopiedState());
		EntityUtils.spawnEjectedItem(world, pos, is);
	}

	public boolean setPowerState(boolean powered)
	{
		if (powered == this.powered)
			return false;

		if (!inTransition)
			this.transitionTimer = powered ? 0 : getDuration();
		this.powered = powered;
		this.inTransition = true;
		//will probably break
		world.setBlockState(pos, getWorld().getBlockState(pos).withProperty(VanishingBlock.TRANSITION, true));

		//		blockDrawn = false;
		//		MalisisCore.message("blockDrawn > false (setPowerState)");

		return true;
	}

	@Override
	public void update() {
		
		if (!inTransition && !powered) {
			if (!world.isRemote) {
				return;
			}
		}
		else if (inTransition) {

			if (powered) // powering => going invisible
			{
				transitionTimer++;
				if (transitionTimer >= getDuration())
				{
					inTransition = false;
					world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
										pos.getX() + 0.5F,
										pos.getY() + 0.5F,
										pos.getZ() + 0.5F,
										0.0F,
										0.0F,
										0.0F);
				}
			}
			else
			// shutting down => going visible
			{
				transitionTimer--;
				if (transitionTimer <= 0)
				{
					inTransition = false;
					TileEntityUtils.notifyUpdate(this);
				}
			}
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		if (nbt.hasKey("BlockID"))
		{
			Block block = Block.getBlockById(nbt.getInteger("BlockId"));
			copiedState = block.getStateFromMeta(nbt.getInteger("BlockMetadata"));
		}
		else
			copiedState = MBlockState.fromNBT(nbt);

		if (nbt.hasKey("copiedTileEntity"))
		{
			initCopiedTileEntity();
			copiedTileEntity.readFromNBT(nbt.getCompoundTag("copiedTileEntity"));
		}

		frameType = VanishingBlock.Type.values()[nbt.getInteger("FrameType")];
		powered = nbt.getBoolean("Powered");
		duration = nbt.getInteger("Duration");
		inTransition = nbt.getBoolean("InTransition");
		transitionTimer = nbt.getInteger("TransitionTimer");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		if (copiedState != null)
		{
			MBlockState.toNBT(nbt, copiedState);
			if (copiedTileEntity != null)
			{
				NBTTagCompound teTag = new NBTTagCompound();
				copiedTileEntity.writeToNBT(teTag);
				nbt.setTag("copiedTileEntity", teTag);
			}
		}
		nbt.setInteger("FrameType", frameType.ordinal());
		nbt.setBoolean("Powered", powered);
		nbt.setInteger("Duration", getDuration());
		nbt.setBoolean("InTransition", inTransition);
		nbt.setInteger("TransitionTimer", transitionTimer);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag()
	{
		return writeToNBT(new NBTTagCompound());
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
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public boolean shouldRenderInPass(int pass)
	{
		return pass == 0;
	}
}
