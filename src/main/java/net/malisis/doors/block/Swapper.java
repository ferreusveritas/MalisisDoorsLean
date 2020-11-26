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
import net.malisis.doors.MalisisDoors.Sounds;
import net.malisis.doors.block.component.DirectionalComponent;
import net.malisis.doors.block.component.PowerComponent;
import net.malisis.doors.block.component.PowerComponent.ComponentType;
import net.malisis.doors.block.component.PowerComponent.InteractionType;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.malisis.doors.tileentity.SwapperTileEntity;
import net.malisis.doors.util.TileEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
public class Swapper extends MalisisBlock implements ITileEntityProvider
{
	public static PropertyBool POWERED = PropertyBool.create("powered");

	public Swapper()
	{
		super(Material.IRON);
		setCreativeTab(MalisisDoors.tab);
		setHardness(3.0F);
		setName("swapper");

		addComponent(new DirectionalComponent(DirectionalComponent.ALL));
		addComponent(new PowerComponent(InteractionType.REDSTONE, ComponentType.RECEIVER));

		if (MalisisDoors.isClient())
		{
			addComponent(IIconProvider	.create(MalisisDoors.modid + ":blocks/", "swapper")
										.withSide(EnumFacing.SOUTH, "swapper_top")
										.build());
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos)
	{
		super.neighborChanged(state, world, pos, neighborBlock, fromPos);

		if (state == world.getBlockState(pos))
			return;

		world.playSound(null, pos, Sounds.portal, SoundCategory.BLOCKS, 0.3F, 0.5F);
		SwapperTileEntity te = TileEntityUtils.getTileEntity(SwapperTileEntity.class, world, pos);
		if (te != null)
			te.swap();
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new SwapperTileEntity();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		SwapperTileEntity te = TileEntityUtils.getTileEntity(SwapperTileEntity.class, world, pos);
		if (te == null)
			return;
		te.dropStoredStates();
	}

	@Override
	public IBlockState getStateFromItemStack(ItemStack itemStack)
	{
		return getDefaultState();
	}
}
