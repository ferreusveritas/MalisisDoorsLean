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

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.MoreObjects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.block.MixedBlock;
import net.malisis.doors.renderer.MixedBlockRenderer;
import net.malisis.doors.tileentity.MixedBlockTileEntity;
import net.malisis.doors.block.MalisisBlock;
import net.malisis.doors.item.MalisisItemBlock;
import net.malisis.doors.renderer.MalisisRendered;
import net.malisis.doors.util.ItemUtils;
import net.malisis.doors.util.MBlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.world.World;

@MalisisRendered(MixedBlockRenderer.class)
public class MixedBlockBlockItem extends MalisisItemBlock
{
	private static BiMap<Item, IBlockState> itemsAllowed = HashBiMap.create();
	static
	{
		itemsAllowed.put(Items.ENDER_PEARL, Blocks.PORTAL.getDefaultState());
		itemsAllowed.put(Items.WATER_BUCKET, Blocks.WATER.getDefaultState());
		itemsAllowed.put(Items.LAVA_BUCKET, Blocks.LAVA.getDefaultState());
	}

	public MixedBlockBlockItem(MixedBlock block)
	{
		super(block);
	}

	@Override
	public void onCreated(ItemStack itemStack, World world, EntityPlayer player)
	{
		itemStack.setTagCompound(new NBTTagCompound());
	}

	public static boolean canBeMixed(ItemStack itemStack)
	{
		if (itemsAllowed.get(itemStack.getItem()) != null)
			return true;

		IBlockState state = ItemUtils.getStateFromItemStack(itemStack);
		return state != null && !(state.getBlock() instanceof MixedBlock)
				&& (state.getRenderType() == EnumBlockRenderType.MODEL || state.getBlock() instanceof MalisisBlock);
	}

	public static ItemStack fromItemStacks(ItemStack is1, ItemStack is2)
	{
		if (!canBeMixed(is1) || !canBeMixed(is2))
			return ItemStack.EMPTY;

		IBlockState state1 = MoreObjects.firstNonNull(itemsAllowed.get(is1.getItem()), ItemUtils.getStateFromItemStack(is1));
		IBlockState state2 = MoreObjects.firstNonNull(itemsAllowed.get(is2.getItem()), ItemUtils.getStateFromItemStack(is2));

		//last check
		if (state1 == null || state2 == null || state1.equals(state2))
			return ItemStack.EMPTY;

		//nbt
		ItemStack itemStack = new ItemStack(MalisisDoors.Blocks.mixedBlock, 1);
		itemStack.setTagCompound(writeNBT(new NBTTagCompound(), state1, state2));
		return itemStack;
	}

	public static ItemStack fromTileEntity(MixedBlockTileEntity te)
	{
		ItemStack itemStack = new ItemStack(MalisisDoors.Blocks.mixedBlock, 1);
		itemStack.setTagCompound(writeNBT(new NBTTagCompound(), te.getState1(), te.getState2()));
		return itemStack;
	}

	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> list, ITooltipFlag tooltipFlag)
	{
		if (itemStack.getTagCompound() == null)
			return;

		Pair<IBlockState, IBlockState> pair = readNBT(itemStack.getTagCompound());

		Item item = itemsAllowed.inverse().get(pair.getLeft());
		ItemStack is1 = item != null ? new ItemStack(item) : ItemUtils.getItemStackFromState(pair.getLeft());
		item = itemsAllowed.inverse().get(pair.getRight());
		ItemStack is2 = item != null ? new ItemStack(item) : ItemUtils.getItemStackFromState(pair.getRight());

		list.addAll(is1.getTooltip(null, tooltipFlag));
		list.addAll(is2.getTooltip(null, tooltipFlag));
	}

	public static Pair<IBlockState, IBlockState> readNBT(NBTTagCompound nbt)
	{
		return new ImmutablePair<>(MBlockState.fromNBT(nbt, "block1", "metadata1"), MBlockState.fromNBT(nbt, "block2", "metadata2"));
	}

	public static NBTTagCompound writeNBT(NBTTagCompound nbt, IBlockState state1, IBlockState state2)
	{
		if (state1 == null)
			state1 = Blocks.STONE.getDefaultState();
		if (state2 == null)
			state2 = Blocks.STONE.getDefaultState();

		MBlockState.toNBT(nbt, state1, "block1", "metadata1");
		MBlockState.toNBT(nbt, state2, "block2", "metadata2");

		return nbt;
	}
}
