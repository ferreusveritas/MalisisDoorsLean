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

package net.malisis.doors.item;

import java.util.List;

import javax.annotation.Nullable;

import net.malisis.doors.DoorDescriptor;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.block.IRegisterable;
import net.malisis.doors.inventory.MalisisTab;
import net.malisis.doors.renderer.DoorRenderer;
import net.malisis.doors.renderer.MalisisRendered;
import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@MalisisRendered(item = DoorRenderer.class)
public class DoorItem extends ItemDoor implements IRegisterable<Item>, IIconProvider
{
	protected DoorDescriptor descriptor;
	@SideOnly(Side.CLIENT)
	protected Icon icon;

	public DoorItem(DoorDescriptor desc)
	{
		super(desc.getBlock());

		this.descriptor = desc;
		this.maxStackSize = desc.getMaxStackSize();
		setName(desc.getRegistryName());
		setUnlocalizedName(desc.getUnlocalizedName());
		//setTextureName(desc.getTextureName());
		setCreativeTab(desc.getTab());

		if (MalisisDoors.isClient() && descriptor.getTextureName() != null)
			icon = Icon.from(descriptor.getModId() + ":items/" + descriptor.getTextureName());
	}

	//for CustomDoor
	public DoorItem()
	{
		super(null);
	}

	public DoorDescriptor getDescriptor(ItemStack itemStack)
	{
		return descriptor;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon()
	{
		return icon;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (side != EnumFacing.UP)
			return EnumActionResult.FAIL;

		ItemStack itemStack = player.getHeldItem(hand);
		IBlockState state = world.getBlockState(pos);
		if (!state.getBlock().isReplaceable(world, pos))
			pos = pos.up();

		Block block = getDescriptor(itemStack).getBlock();
		if (block == null)
		{
			MalisisDoors.log.error("Can't place Door : block is null for " + itemStack);
			return EnumActionResult.FAIL;
		}

		if (!player.canPlayerEdit(pos, side, itemStack) || !player.canPlayerEdit(pos.up(), side, itemStack))
			return EnumActionResult.FAIL;

		if (!block.canPlaceBlockAt(world, pos))
			return EnumActionResult.FAIL;

		placeDoor(world, pos, EnumFacing.fromAngle(player.rotationYaw), block, false);
		itemStack.shrink(1);
		block.onBlockPlacedBy(world, pos, world.getBlockState(pos), player, itemStack);
		return EnumActionResult.SUCCESS;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if (stack.getTagCompound() == null)
			return;

		tooltip.add(TextFormatting.WHITE + I18n.format("door_movement." + stack.getTagCompound().getString("movement")));
	}

	@Override
	public DoorItem setCreativeTab(CreativeTabs tab)
	{
		super.setCreativeTab(tab);
		if (tab instanceof MalisisTab)
			((MalisisTab) tab).addItem(this);
		return this;
	}
}
