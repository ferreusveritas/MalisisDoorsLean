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

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.MalisisDoors.Items;
import net.malisis.doors.MalisisDoors.Sounds;
import net.malisis.doors.inventory.MalisisInventory;
import net.malisis.doors.ProxyAccess;
import net.malisis.doors.item.VanishingBlockItem;
import net.malisis.doors.renderer.DefaultRenderer;
import net.malisis.doors.renderer.MalisisRendered;
import net.malisis.doors.renderer.VanishingBlockRenderer;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.malisis.doors.renderer.icon.provider.IconProviderBuilder;
import net.malisis.doors.tileentity.VanishingDiamondTileEntity;
import net.malisis.doors.tileentity.VanishingTileEntity;
import net.malisis.doors.util.AABBUtils;
import net.malisis.doors.util.EntityUtils;
import net.malisis.doors.util.IMSerializable;
import net.malisis.doors.util.TileEntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * @author Ordinastie
 *
 */
@MalisisRendered(block = VanishingBlockRenderer.class, item = DefaultRenderer.Block.class)
public class VanishingBlock extends MalisisBlock implements ITileEntityProvider {
	
	public enum Type implements IMSerializable {
		WOOD,
		IRON,
		GOLD,
		DIAMOND;
	};
	
	public static final PropertyEnum<Type> TYPE = PropertyEnum.create("type", Type.class);
	public static PropertyBool POWERED = PropertyBool.create("powered");
	public static PropertyBool TRANSITION = PropertyBool.create("transition");
	
	public static int renderId = -1;
	public int renderPass = -1;
	
	protected VanishingBlock(String name) {
		super(Material.WOOD);
		setName(name);
		setCreativeTab(MalisisDoors.tab);
		setHardness(0.5F);
		
		setDefaultState(blockState.getBaseState().withProperty(TYPE, Type.WOOD).withProperty(POWERED, false).withProperty(	TRANSITION,
				false));
		
		if (MalisisDoors.isClient()) {
			IconProviderBuilder builder = IIconProvider.create(MalisisDoors.modid + ":blocks/", "vanishing_block_wood").forProperty(TYPE);
			for (Type type : Type.values()) {
				builder.withValue(type, "vanishing_block_" + type.getName().toLowerCase());
			}
			addComponent(builder.build());
		}
		
	}
	
	public VanishingBlock() {
		this("vanishing_block");
	}
	
	@Override
	public Item getItem(Block block) {
		return new VanishingBlockItem(this);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, TYPE, POWERED, TRANSITION);
	}
	
	/**
	 * Check if block at x, y, z is a powered VanishingBlock
	 */
	public boolean isPowered(World world, BlockPos pos) {
		return isPowered(world.getBlockState(pos));
	}
	
	public boolean isPowered(IBlockState state) {
		return state.getBlock() == this && state.getValue(POWERED);
	}
	
	public boolean shouldDefer(VanishingTileEntity te) {
		return te != null && te.getCopiedState() != null && !te.isPowered() && !te.isInTransition();
	}
	
	/**
	 * Set the power state for the block at x, y, z
	 */
	public void setPowerState(World world, BlockPos pos, boolean powered) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) // block is VanishingBlock ?
			return;
		if (isPowered(state) == powered) // same power state?
			return;
		
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (te == null)
			return;
		
		te.setPowerState(powered);
		world.setBlockState(pos, state.withProperty(POWERED, powered));
		world.scheduleBlockUpdate(pos, this, 1, 0);
	}
	
	/**
	 * Check if the block is available for propagation of power state
	 */
	public boolean shouldPropagate(World world, BlockPos pos, VanishingTileEntity source) {
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) {// block is VanishingBlock ?
			return false;
		}
		
		Type sourceType = source.getType();
		
		if (sourceType == Type.DIAMOND) {
			return false;
		}
		
		if (sourceType == Type.WOOD)
			return true;
		
		VanishingTileEntity dest = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (dest == null)
			return false;
		
		if (source.getCopiedState() == null || dest.getCopiedState() == null)
			return true;
		
		if (sourceType == Type.IRON && source.getCopiedState().getBlock() == dest.getCopiedState().getBlock())
			return true;
		
		if (sourceType == Type.GOLD && source.getCopiedState().equals(dest.getCopiedState()))
			return true;
		
		return false;
	}
	
	/**
	 * Propagate power state in all six direction
	 */
	public void propagateState(World world, BlockPos pos)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		for (EnumFacing dir : EnumFacing.values()) {
			if (shouldPropagate(world, pos.offset(dir), te)) {
				this.setPowerState(world, pos.offset(dir), te.isPowered());
			}
		}
	}
	
	// #region Events
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		
		if (te == null) {
			return false;
		}
		
		if(te instanceof VanishingDiamondTileEntity){
			VanishingDiamondTileEntity ted = (VanishingDiamondTileEntity) te;
			
			if (world.isRemote) {
				return true;
			}
			
			if (!EntityUtils.isEquipped(player, Items.vanishingCopierItem, hand)) {
				MalisisInventory.open((EntityPlayerMP) player, ted);
				return true;
			}
			
			if (player.isSneaking()) {
				ted.copyOptions(player.getHeldItem(hand));
			}
			else {
				ted.pasteOptions(player.getHeldItem(hand), player, side, hitX, hitY, hitZ);
			}
			return true;
		}
		
		
		if (player.isSneaking()) {
			te.applyItemStack(null, player, hand, side, hitX, hitY, hitZ);
		}
		else if (te.getCopiedState() == null) {
			return te.applyItemStack(player.getHeldItem(hand), player, hand, side, hitX, hitY, hitZ);
		}
		
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos)
	{
		boolean powered = world.isBlockIndirectlyGettingPowered(pos) != 0;
		if (powered || (neighborBlock.getDefaultState().canProvidePower() && neighborBlock != this))
		{
			if (isPowered(world, pos) != powered)
				world.playSound(null, pos, Sounds.portal, SoundCategory.BLOCKS, 0.3F, 0.5F);
			this.setPowerState(world, pos, powered);
		}
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		this.propagateState(world, pos);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (te != null && te.getCopiedState() != null)
			te.ejectCopiedState();
		
		world.removeTileEntity(pos);
	}
	
	// #end Events
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state, BoundingBoxType type)
	{
		if (world == null)
			return AABBUtils.identity();
		
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (te == null || te.isPowered() || te.isInTransition())
			return null;
		
		return AABBUtils.identity();
	}
	
	@Override
	@Nullable
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (!shouldDefer(te))
			return super.getCollisionBoundingBox(state, world, pos);
		
		return te.getCopiedState().getCollisionBoundingBox(ProxyAccess.get(world), pos);
		
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, @Nullable Entity collidingEntity, boolean useActualState)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (!shouldDefer(te))
		{
			super.addCollisionBoxToList(state, world, pos, mask, list, collidingEntity, false);
			return;
		}
		
		te.getCopiedState().addCollisionBoxToList((World) ProxyAccess.get(world), pos, mask, list, collidingEntity, useActualState);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (!shouldDefer(te))
			return super.getBoundingBox(state, world, pos);
		
		return te.getCopiedState().getBoundingBox(ProxyAccess.get(world), pos);
		
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (!shouldDefer(te))
			return super.getSelectedBoundingBox(state, world, pos);
		
		return te.getCopiedState().getSelectedBoundingBox((World) ProxyAccess.get(world), pos);
	}
	
	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d src, Vec3d dest)
	{
		VanishingTileEntity te = TileEntityUtils.getTileEntity(VanishingTileEntity.class, world, pos);
		if (!shouldDefer(te))
			return super.collisionRayTrace(state, world, pos, src, dest);
		
		World proxy = (World) ProxyAccess.get(world);
		//prevent infinite recursion
		if (proxy == world && te.getCopiedState().getBlock() instanceof IBoundingBox)
			return super.collisionRayTrace(state, world, pos, src, dest);
		
		return te.getCopiedState().collisionRayTrace(proxy, pos, src, dest);
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		// VanishingDiamondBlock has its own unused itemBlock, but we don't want it
		return new ItemStack(Item.getItemFromBlock(MalisisDoors.Blocks.vanishingBlock), 1, damageDropped(state));
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		for (Type type : Type.values()) {
			list.add(new ItemStack(this, 1, type.ordinal()));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return state.getValue(TYPE).ordinal();
	}
	
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public float getAmbientOcclusionLightValue(IBlockState state)
	{
		return 0.9F;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(TYPE, Type.values()[meta & 3]).withProperty(POWERED, (meta & 8) != 0);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(TYPE).ordinal() + (state.getValue(POWERED) ? 8 : 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		Type type = getStateFromMeta(metadata).getValue(TYPE);
		
		if(type == Type.DIAMOND) {
			return new VanishingDiamondTileEntity();
		}
		
		return new VanishingTileEntity(getStateFromMeta(metadata).getValue(TYPE));
	}
	
	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
	{
		return true;
	}
}
