package net.malisis.doors.block;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.block.BoundingBoxType;
import net.malisis.doors.block.MalisisBlock;
import net.malisis.doors.block.component.DirectionalComponent;
import net.malisis.doors.block.component.DirectionalComponent.IPlacement;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class PlayerSensor extends MalisisBlock {
	
	public static PropertyBool POWERED = PropertyBool.create("powered");
	
	private static AxisAlignedBB AABB_SIDE = new AxisAlignedBB(0.125F, 0.125F, 0, 0.875F, 0.25F, 0.125F);
	private static AxisAlignedBB AABB_BOTTOM = new AxisAlignedBB(0.25F, 0.25F, 0, 0.75F, 0.75F, 0.0625F);
	
	public PlayerSensor() {
		super(Material.CIRCUITS);
		setCreativeTab(MalisisDoors.tab);
		setName("player_sensor");
		setTexture(MalisisDoors.modid + ":blocks/player_sensor");
		
		addComponent(new DirectionalComponent(DirectionalComponent.ALL, IPlacement.BLOCKSIDE));
		
		setDefaultState(getDefaultState().withProperty(POWERED, false));
	}
	
	@Override
	protected List<IProperty<?>> getProperties() {
		return Lists.newArrayList(POWERED);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state, BoundingBoxType type) {
		if (type == BoundingBoxType.COLLISION) {
			return null;
		}
		
		//world == null -> item
		return DirectionalComponent.getDirection(state).getAxis() == Axis.Y && world != null ? AABB_BOTTOM : AABB_SIDE;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		side = side.getOpposite();
		return world.isSideSolid(pos.offset(side), side);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		for (EnumFacing side : EnumFacing.values()) {
			if (world.isSideSolid(pos.offset(side), side)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos) {
		EnumFacing dir = DirectionalComponent.getDirection(world, pos).getOpposite();
		if (!world.isSideSolid(pos.offset(dir), dir)) {
			dropBlockAsItem(world, pos, getDefaultState(), 0);
			world.setBlockToAir(pos);
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (isPowered(state)) {
			notifyPower(world, pos, state);
		}
	}
	
	@Override
	public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return isPowered(state) ? 15 : 0;
	}
	
	@Override
	public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return (isPowered(state) && DirectionalComponent.getDirection(world, pos) == side) ? 15 : 0;
	}
	
	@Override
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		world.scheduleBlockUpdate(pos, this, 5, 0);
	}
	
	public AxisAlignedBB getDetectionBox(IBlockAccess world, BlockPos pos) {
		EnumFacing dir = DirectionalComponent.getDirection(world, pos);
		double x1 = pos.getX(), x2 = pos.getX();
		double z1 = pos.getZ(), z2 = pos.getZ();
		int yOffset = 1;
		int factor = -1;
		
		if (dir == EnumFacing.EAST) {
			x1 -= 1;
			x2 += 2;
			z2 += 1;
		}
		else if (dir == EnumFacing.WEST) {
			x1 -= 1;
			x2 += 2;
			z2 += 1;
		}
		else if (dir == EnumFacing.NORTH) {
			x2 += 1;
			z1 -= 1;
			z2 += 2;
		}
		else if (dir == EnumFacing.SOUTH) {
			x2 += 1;
			z1 -= 1;
			z2 += 2;
		}
		else if (dir == EnumFacing.UP) {
			x2 += 1;
			z2 += 1;
			factor = 1;
		}
		else if (dir == EnumFacing.DOWN) {
			x2 += 1;
			z2 += 1;
		}
		
		boolean isAir = world.isAirBlock(pos.up(factor));
		while (isAir && yOffset < 6) {
			isAir = world.isAirBlock(pos.up(factor * yOffset++));
		}
		
		return new AxisAlignedBB(x1, pos.getY(), z1, x2, pos.up(factor * yOffset++).getY(), z2);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		boolean powered = isPowered(state);
		if (world.isRemote) {
			return;
		}
		
		world.scheduleBlockUpdate(pos, this, 5, 0);
		
		List<EntityPlayer> list = world.getEntitiesWithinAABB(EntityPlayer.class, this.getDetectionBox(world, pos));
		boolean gettingPowered = list != null && !list.isEmpty();
		
		if (powered != gettingPowered) {
			world.setBlockState(pos, state.withProperty(POWERED, gettingPowered));
			notifyPower(world, pos, state);
		}
	}
	
	private void notifyPower(World world, BlockPos pos, IBlockState state) {
		world.notifyNeighborsOfStateChange(pos, this, true);
		world.notifyNeighborsOfStateChange(pos.offset(DirectionalComponent.getDirection(state).getOpposite()), this, true);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	public static boolean isPowered(IBlockState state) {
		return state.getValue(POWERED);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(POWERED, (meta & 8) != 0);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return super.getMetaFromState(state) + (isPowered(state) ? 8 : 0);
	}
	
}
