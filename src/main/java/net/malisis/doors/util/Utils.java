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
import java.util.Optional;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.asm.AsmUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * @author Ordinastie
 *
 */
public class Utils
{
	/**
	 * Checks if is the {@link Chunk} is loaded for the specified {@link BlockPos}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @return true, if is chunk loaded
	 */
	public static boolean isChunkLoaded(World world, BlockPos pos)
	{
		return getLoadedChunk(world, pos).isPresent();
	}

	/**
	 * Gets an {@link Optional} for the loaded {@link Chunk} at the specified {@link BlockPos}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @return the loaded chunk
	 */
	public static Optional<Chunk> getLoadedChunk(World world, BlockPos pos)
	{
		if (world.getChunkProvider() == null)
			return Optional.empty();

		return Optional.ofNullable(world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4));
	}

	/**
	 * Gets the client world.
	 *
	 * @return the client world
	 */
	@SideOnly(Side.CLIENT)
	public static World getClientWorld()
	{
		return Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().world : null;
	}

	/**
	 * Gets the client player.
	 *
	 * @return the client player
	 */
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft() != null ? Minecraft.getMinecraft().player : null;
	}

	/**
	 * Creates a {@link ResourceLocation} from the specified name.<br>
	 * The name is split on ':' to find the modid.<br>
	 * If the modid is not specified, the current active mod container is used, or "minecraft" if none is found.
	 *
	 * @param name the name
	 * @return the resource location
	 */
	public static ResourceLocation getResourceLocation(String name)
	{
		int index = name.lastIndexOf(':');
		String res = null;
		String modid = null;
		if (index == -1)
		{
			ModContainer container = Loader.instance().activeModContainer();
			modid = container != null ? container.getModId() : "minecraft";
			res = name;
		}
		else
		{
			modid = name.substring(0, index);
			res = name.substring(index + 1);
		}

		return new ResourceLocation(modid, res);
	}

	private static Field registryName = AsmUtils.changeFieldAccess(IForgeRegistryEntry.Impl.class, "registryName");

	public static void silentRegistryName(IForgeRegistryEntry<?> object, String name)
	{
		ResourceLocation rl = Utils.getResourceLocation(name);
		String mod = Loader.instance().activeModContainer().getModId();
		if (rl.getResourceDomain() == mod.toLowerCase())
			MalisisDoors.log.warn("Setting registry name {} for already active container {}. Use setRegistryName() instead.", rl, mod);
		try
		{
			registryName.set(object, rl);
		}
		catch (IllegalArgumentException | IllegalAccessException e)
		{
			MalisisDoors.log.error("Failed to set registry name {} for {}.", rl, object.getClass(), e);
		}
	}

}
