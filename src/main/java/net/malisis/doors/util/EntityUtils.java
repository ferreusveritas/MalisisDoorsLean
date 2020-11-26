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

package net.malisis.doors.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.asm.AsmUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Utility class for Entities.
 *
 * @author Ordinastie
 *
 */

public class EntityUtils
{
	private static EnumFacing[] facings = new EnumFacing[] {	EnumFacing.NORTH,
																EnumFacing.EAST,
																EnumFacing.SOUTH,
																EnumFacing.WEST,
																EnumFacing.UP,
																EnumFacing.DOWN };

	private static Field playersWatchingChunk;
	static
	{
		try
		{
			Class<?> clazz = Class.forName("net.minecraft.server.management.PlayerChunkMapEntry");
			playersWatchingChunk = AsmUtils.changeFieldAccess(clazz, "players", "field_187283_c");
		}
		catch (ClassNotFoundException e)
		{
			MalisisDoors.log.error("Failed to get PlayerChunkMap class.", e);
		}

	}

	/**
	 * Eject a new item corresponding to the {@link ItemStack}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @param itemStack the item stack
	 */
	public static void spawnEjectedItem(World world, BlockPos pos, ItemStack itemStack)
	{
		if (itemStack == null || world.isRemote)
			return;

		float rx = world.rand.nextFloat() * 0.8F + 0.1F;
		float ry = world.rand.nextFloat() * 0.8F + 0.1F;
		float rz = world.rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityItem = new EntityItem(world, pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz, itemStack);

		float factor = 0.05F;
		entityItem.motionX = world.rand.nextGaussian() * factor;
		entityItem.motionY = world.rand.nextGaussian() * factor + 0.2F;
		entityItem.motionZ = world.rand.nextGaussian() * factor;
		world.spawnEntity(entityItem);

	}

	/**
	 * Finds a player by its UUID. FIXME
	 *
	 * @param uuid the uuid
	 * @return the player
	 */
	public static EntityPlayerMP findPlayerFromUUID(UUID uuid)
	{
		//		List<EntityPlayerMP> listPlayers = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		//
		//		for (EntityPlayerMP player : listPlayers)
		//			if (player.getUniqueID().equals(uuid))
		//				return player;

		return null;
	}

	/**
	 * Gets the {@link EnumFacing} the {@link Entity} is currently facing.
	 *
	 * @param entity the entity
	 * @return the direction
	 */
	public static EnumFacing getEntityFacing(Entity entity)
	{
		return getEntityFacing(entity, false);
	}

	/**
	 * Gets the {@link EnumFacing} the {@link Entity} is currently facing.<br>
	 * If <b>sixWays</b> is <code>true</code>, the direction can be {@link EnumFacing#UP UP} or {@link EnumFacing#DOWN DOWN} if the entity
	 * is looking up or down.
	 *
	 * @param entity the entity
	 * @param sixWays whether to consider UP and DOWN for directions
	 * @return the direction
	 */
	public static EnumFacing getEntityFacing(Entity entity, boolean sixWays)
	{
		return facings[getEntityRotation(entity, sixWays)];
	}

	/**
	 * Gets the entity rotation based on where it's currently facing.
	 *
	 * @param entity the entity
	 * @return the entity rotation
	 */
	public static int getEntityRotation(Entity entity)
	{
		return getEntityRotation(entity, false);
	}

	/**
	 * Gets the entity rotation based on where it's currently facing.
	 *
	 * @param entity the entity
	 * @param sixWays the six ways
	 * @return the entity rotation
	 */
	public static int getEntityRotation(Entity entity, boolean sixWays)
	{
		if (entity == null)
			return 6;

		float pitch = entity.rotationPitch;
		if (sixWays && pitch < -45)
			return 4;
		if (sixWays && pitch > 45)
			return 5;

		return (MathHelper.floor(entity.rotationYaw * 4.0F / 360.0F + 0.5D) + 2) & 3;
	}

	/**
	 * Checks if is the {@link Item} is equipped for the player.
	 *
	 * @param player the player
	 * @param item the item
	 * @return true, if is equipped
	 */
	public static boolean isEquipped(EntityPlayer player, Item item, EnumHand hand)
	{
		return player != null && player.getHeldItem(hand) != null && player.getHeldItem(hand).getItem() == item;
	}

	/**
	 * Checks if is the {@link Item} contained in the {@link ItemStack} is equipped for the player.
	 *
	 * @param player the player
	 * @param itemStack the item stack
	 * @return true, if is equipped
	 */
	public static boolean isEquipped(EntityPlayer player, ItemStack itemStack, EnumHand hand)
	{
		return isEquipped(player, itemStack != null ? itemStack.getItem() : null, hand);
	}

	/**
	 * Gets the list of players currently watching the {@link Chunk}.
	 *
	 * @param chunk the chunk
	 * @return the players watching chunk
	 */
	public static List<EntityPlayerMP> getPlayersWatchingChunk(Chunk chunk)
	{
		return getPlayersWatchingChunk((WorldServer) chunk.getWorld(), chunk.x, chunk.z);
	}

	/**
	 * Gets the list of players currently watching the chunk at the coordinate.
	 *
	 * @param world the world
	 * @param x the x
	 * @param z the z
	 * @return the players watching chunk
	 */
	@SuppressWarnings("unchecked")
	public static List<EntityPlayerMP> getPlayersWatchingChunk(WorldServer world, int x, int z)
	{
		if (playersWatchingChunk == null)
			return new ArrayList<>();

		try
		{
			PlayerChunkMapEntry entry = world.getPlayerChunkMap().getEntry(x, z);
			if (entry == null)
				return Lists.newArrayList();
			return (List<EntityPlayerMP>) playersWatchingChunk.get(entry);
		}
		catch (ReflectiveOperationException e)
		{
			MalisisDoors.log.info("Failed to get players watching chunk :", e);
			return new ArrayList<>();
		}
	}

	/**
	 * Gets the render view offset for the current view entity.
	 *
	 * @param partialTick the partial tick
	 * @return the render view offset
	 */
	public static Point getRenderViewOffset(float partialTick)
	{
		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		if (partialTick == 0)
			return new Point(entity.posX, entity.posY, entity.posZ);

		double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick;
		double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick;
		double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick;
		return new Point(x, y, z);
	}

	/**
	 * Adds the destroy effects into the world for the specified {@link IBlockState}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @param particleManager the effect renderer
	 * @param states the states
	 */
	@SideOnly(Side.CLIENT)
	public static void addDestroyEffects(World world, BlockPos pos, ParticleManager particleManager, IBlockState... states)
	{
		if (ArrayUtils.isEmpty(states))
			states = new IBlockState[] { world.getBlockState(pos) };

		byte nb = 4;
		ParticleDigging.Factory factory = new ParticleDigging.Factory();

		for (int i = 0; i < nb; ++i)
		{
			for (int j = 0; j < nb; ++j)
			{
				for (int k = 0; k < nb; ++k)
				{
					double fxX = pos.getX() + (i + 0.5D) / nb;
					double fxY = pos.getY() + (j + 0.5D) / nb;
					double fxZ = pos.getZ() + (k + 0.5D) / nb;

					int id = Block.getStateId(states[world.rand.nextInt(states.length)]);

					ParticleDigging fx = (ParticleDigging) factory.createParticle(	0,
																					world,
																					fxX,
																					fxY,
																					fxZ,
																					fxX - pos.getX() - 0.5D,
																					fxY - pos.getY() - 0.5D,
																					fxZ - pos.getZ() - 0.5D,
																					id);
					particleManager.addEffect(fx);
				}
			}
		}
	}

	/**
	 * Adds the hit effects into the world for the specified {@link IBlockState}.
	 *
	 * @param world the world
	 * @param target the target
	 * @param particleManager the effect renderer
	 * @param states the states
	 */
	@SideOnly(Side.CLIENT)
	public static void addHitEffects(World world, RayTraceResult target, ParticleManager particleManager, IBlockState... states)
	{
		BlockPos pos = target.getBlockPos();
		if (ArrayUtils.isEmpty(states))
			states = new IBlockState[] { world.getBlockState(pos) };

		IBlockState baseState = world.getBlockState(pos);
		if (baseState.getRenderType() != EnumBlockRenderType.INVISIBLE)
			return;

		double fxX = pos.getX() + world.rand.nextDouble();
		double fxY = pos.getY() + world.rand.nextDouble();
		double fxZ = pos.getZ() + world.rand.nextDouble();

		AxisAlignedBB aabb = baseState.getBoundingBox(world, pos);
		switch (target.sideHit)
		{
			case DOWN:
				fxY = pos.getY() + aabb.minY - 0.1F;
				break;
			case UP:
				fxY = pos.getY() + aabb.maxY + 0.1F;
				break;
			case NORTH:
				fxZ = pos.getZ() + aabb.minZ - 0.1F;
				break;
			case SOUTH:
				fxZ = pos.getZ() + aabb.maxY + 0.1F;
				break;
			case EAST:
				fxX = pos.getX() + aabb.maxX + 0.1F;
				break;
			case WEST:
				fxX = pos.getX() + aabb.minX + 0.1F;
				break;
			default:
				break;
		}

		int id = Block.getStateId(states[world.rand.nextInt(states.length)]);

		ParticleDigging.Factory factory = new ParticleDigging.Factory();
		ParticleDigging fx = (ParticleDigging) factory.createParticle(0, world, fxX, fxY, fxZ, 0, 0, 0, id);
		fx.multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F);
		particleManager.addEffect(fx);
	}
}
