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

package net.malisis.doors;

import java.util.WeakHashMap;

import lombok.experimental.Delegate;
import net.malisis.doors.tileentity.VanishingTileEntity;
import net.malisis.doors.MalisisDoors;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ProxyAccess
{
	private static WeakHashMap<IBlockAccess, IBlockAccess> cache = new WeakHashMap<>();
	private static boolean worldInstanciationFailed = false;

	private interface IProxyAccess
	{
		public IBlockState getBlockState(BlockPos pos);

		public TileEntity getTileEntity(BlockPos pos);

		public boolean setBlockState(BlockPos pos, IBlockState state, int flag);
	}

	public static IBlockAccess get(IBlockAccess world) {
		if (world == null) {
			return null;
		}
		
		cache.clear();
		IBlockAccess proxy = cache.get(world);
		if (proxy == null) {
			if (world instanceof World) {
				if (worldInstanciationFailed) {
					return world;
				}
				try {
					proxy = new ProxyWorld((World) world);
				}
				catch (Exception e) {
					MalisisDoors.log.error("[ProxyAccess] Proxy world instanciation failed :", e);
					worldInstanciationFailed = true;
					return world;
				}
			}
			else {
				proxy = new ProxyBlockAccess(world);
			}
			cache.put(world, proxy);
		}
		return proxy;
	}

	public static VanishingTileEntity getVanishingTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof VanishingTileEntity) {
			return (VanishingTileEntity) te;
		}
		return null;
	}

	public static IBlockState getBlockState(IBlockAccess world, BlockPos pos) {
		VanishingTileEntity te = getVanishingTileEntity(world, pos);
		if (te != null) {
			return te.getCopiedState() != null ? te.getCopiedState() : Blocks.AIR.getDefaultState();
		}
		return world.getBlockState(pos);
	}

	public static TileEntity getTileEntity(IBlockAccess world, BlockPos pos) {
		VanishingTileEntity te = getVanishingTileEntity(world, pos);
		if (te != null) {
			return te.getCopiedTileEntity();
		}
		return world.getTileEntity(pos);
	}

	/**
	 * ProxyBlockAccess
	 */
	private static class ProxyBlockAccess implements IBlockAccess {
		@Delegate(excludes = IProxyAccess.class)
		public IBlockAccess original;

		public ProxyBlockAccess(IBlockAccess world) {
			original = world;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos)
		{
			return ProxyAccess.getBlockState(original, pos);
		}

		@Override
		public TileEntity getTileEntity(BlockPos pos)
		{
			return ProxyAccess.getTileEntity(original, pos);
		}

		@Override
		public int getCombinedLight(BlockPos pos, int lightValue) {
			return original.getCombinedLight(pos, lightValue);
		}

		@Override
		public boolean isAirBlock(BlockPos pos) {
			return original.isAirBlock(pos);
		}

		@Override
		public Biome getBiome(BlockPos pos) {
			return original.getBiome(pos);
		}

		@Override
		public int getStrongPower(BlockPos pos, EnumFacing direction) {
			return original.getStrongPower(pos, direction);
		}

		@Override
		public WorldType getWorldType() {
			return original.getWorldType();
		}

		@Override
		public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
			return original.isSideSolid(pos, side, _default);
		}
	}

	/**
	 * ProxyWorld
	 */
	private static class ProxyWorld extends World
	{
		@Delegate(excludes = IProxyAccess.class)
		public World original;

		public ProxyWorld(World world)
		{
			super(world.getSaveHandler(), world.getWorldInfo(), world.provider, (Profiler) null, world.isRemote);
			original = world;
		}

		@Override
		public IBlockState getBlockState(BlockPos pos)
		{
			return ProxyAccess.getBlockState(original, pos);
		}

		@Override
		public TileEntity getTileEntity(BlockPos pos)
		{
			return ProxyAccess.getTileEntity(original, pos);
		}

		@Override
		public boolean setBlockState(BlockPos pos, IBlockState state, int flag)
		{
			VanishingTileEntity te = ProxyAccess.getVanishingTileEntity(original, pos);
			if (te != null)
			{
				te.setBlockState(state);
				return true;
			}

			return original.setBlockState(pos, state, flag);
		}

		@Override
		public Chunk getChunkFromChunkCoords(int chunkX, int chunkZ) {
			return original.getChunkFromChunkCoords(chunkX, chunkZ);
		}
		
		@Override
		protected IChunkProvider createChunkProvider()
		{
			return null;
		}
		
		 protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
		 {
			 return true;
		 }
	}
}