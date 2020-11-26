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
import net.malisis.doors.tileentity.DoorFactoryTileEntity;
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

/**
 * @author Ordinastie
 *
 */
public class DoorFactory extends MalisisBlock implements ITileEntityProvider
{
	public DoorFactory()
	{
		super(Material.IRON);
		setCreativeTab(MalisisDoors.tab);
		setName("door_factory");
		setHardness(3.0F);

		addComponent(new DirectionalComponent());

		if (MalisisDoors.isClient())
			addComponent(IIconProvider	.create(MalisisDoors.modid + ":blocks/", "door_factory_side")
										.withSide(EnumFacing.SOUTH, "door_factory")
										.build());
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
			return true;

		if (player.isSneaking())
			return false;

		DoorFactoryTileEntity te = TileEntityUtils.getTileEntity(DoorFactoryTileEntity.class, world, pos);
		MalisisInventory.open((EntityPlayerMP) player, te);
		return true;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		DoorFactoryTileEntity provider = TileEntityUtils.getTileEntity(DoorFactoryTileEntity.class, world, pos);
		if (provider != null)
			provider.breakInventories(world, pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2)
	{
		return new DoorFactoryTileEntity();
	}

}
