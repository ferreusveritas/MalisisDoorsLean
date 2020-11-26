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

package net.malisis.doors.block;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.block.component.DirectionalComponent;
import net.malisis.doors.inventory.MalisisInventory;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.malisis.doors.tileentity.BlockMixerTileEntity;
import net.malisis.doors.util.TileEntityUtils;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMixer extends MalisisBlock implements ITileEntityProvider
{
	public BlockMixer()
	{
		super(Material.IRON);
		setCreativeTab(MalisisDoors.tab);
		setHardness(3.0F);
		setName("block_mixer");

		addComponent(new DirectionalComponent());

		if (MalisisDoors.isClient())
		{
			addComponent(IIconProvider	.create(MalisisDoors.modid + ":blocks/", "block_mixer_side")
										.withSide(EnumFacing.SOUTH, "block_mixer")
										.build());
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
			return true;

		if (player.isSneaking())
			return false;

		BlockMixerTileEntity te = TileEntityUtils.getTileEntity(BlockMixerTileEntity.class, world, pos);
		MalisisInventory.open((EntityPlayerMP) player, te);
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		BlockMixerTileEntity provider = TileEntityUtils.getTileEntity(BlockMixerTileEntity.class, world, pos);
		if (provider != null)
			provider.breakInventories(world, pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metdata)
	{
		return new BlockMixerTileEntity();
	}
}
