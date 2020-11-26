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

package net.malisis.doors.util.chunkblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import gnu.trove.procedure.TLongProcedure;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.block.IComponent;
import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.registry.MalisisRegistry;
import net.malisis.doors.util.MBlockPos;
import net.malisis.doors.util.MBlockState;
import net.malisis.doors.util.Utils;
import net.malisis.doors.util.WeakNested;
import net.malisis.doors.util.callback.CallbackResult;
import net.malisis.doors.util.callback.ICallback.CallbackOption;
import net.malisis.doors.util.callback.ICallback.Priority;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class is the entry point for blocks that need to stored inside a chunk for later processing.<br>
 * The static methods are called via ASM which then call the process for the corresponding server or client instance.
 *
 * @author Ordinastie
 *
 */
@AutoLoad
public class ChunkBlockHandler
{
	private static ChunkBlockHandler instance = new ChunkBlockHandler();

	private WeakNested.Set<Chunk, BlockPos> serverChunks = new WeakNested.Set<>(Sets::newHashSet);
	private WeakNested.Set<Chunk, BlockPos> clientChunks = new WeakNested.Set<>(Sets::newHashSet);

	//	private Map<Chunk, List<BlockPos>> serverChunks = new WeakHashMap<>();
	//	private Map<Chunk, List<BlockPos>> clientChunks = new WeakHashMap<>();

	public ChunkBlockHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
		MalisisRegistry.onPreSetBlock(this::handleChunkBlock, CallbackOption.of(Priority.LOWEST));
	}

	public WeakNested.Set<Chunk, BlockPos> chunks(Chunk chunk)
	{
		return chunk.getWorld().isRemote ? clientChunks : serverChunks;
	}

	/**
	 * Gets all the coordinates stored in the chunk.<br>
	 * If no coordinates are stored for the chunk, saves the newList for it.
	 *
	 * @param chunk the chunk
	 * @return the coords
	 */
	//	private Optional<List<BlockPos>> getCoords(Chunk chunk, List<BlockPos> newList)
	//	{
	//		Optional<List<BlockPos>> o = getCoords(chunk);
	//		if (o.isPresent())
	//			return o;
	//
	//		(chunk.getWorld().isRemote ? clientChunks : serverChunks).put(chunk, newList);
	//		return Optional.of(newList);
	//	}

	/**
	 * Gets all the coordinates stored in the chunk.
	 *
	 * @param chunk the chunk
	 * @return the coords
	 */
	//	public Optional<List<BlockPos>> getCoords(Chunk chunk)
	//	{
	//		Map<Chunk, List<BlockPos>> chunks = chunk.getWorld().isRemote ? clientChunks : serverChunks;
	//		return Optional.ofNullable(chunks.get(chunk));
	//	}

	/**
	 * Stores the coordinate in the chunk data if newState blocks has a {@link IChunkBlock} component.<br>
	 * Removes the stored coordinate from the chunk data if oldState has {@link IChunkBlock} component.
	 *
	 * @param chunk the chunk
	 * @param pos the pos
	 * @param oldState the old state
	 * @param newState the new state
	 * @return the callback result
	 */
	private CallbackResult<Void> handleChunkBlock(Chunk chunk, BlockPos pos, IBlockState oldState, IBlockState newState)
	{
		IChunkBlock cb = IComponent.getComponent(IChunkBlock.class, oldState.getBlock());
		if (cb != null)
			removeCoord(chunk.getWorld(), pos, cb.blockRange());
		//TODO: use post ?
		cb = IComponent.getComponent(IChunkBlock.class, newState.getBlock());
		if (cb != null)
			addCoord(chunk.getWorld(), pos, cb.blockRange());
		return CallbackResult.noResult();
	}

	/**
	 * Adds a coordinate for the {@link Chunk Chunks} around {@link BlockPos}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @param size the size
	 */
	private void addCoord(World world, BlockPos pos, int size)
	{
		getAffectedChunks(world, pos.getX(), pos.getZ(), size).forEach(c -> addCoord(c, pos));
	}

	/**
	 * Adds a coordinate for the specified {@link Chunk}.
	 *
	 * @param chunk the chunk
	 * @param pos the pos
	 */
	private void addCoord(Chunk chunk, BlockPos pos)
	{
		chunks(chunk).add(chunk, pos);
	}

	/**
	 * Removes a coordinate from the {@link Chunk}s around the {@link MBlockPos}.
	 *
	 * @param world the world
	 * @param pos the pos
	 * @param size the size
	 */
	private void removeCoord(World world, BlockPos pos, int size)
	{
		getAffectedChunks(world, pos.getX(), pos.getZ(), size).forEach(c -> removeCoord(c, pos));
	}

	/**
	 * Removes a coordinate from the specified {@link Chunk}.
	 *
	 * @param chunk the chunk
	 * @param pos the pos
	 */
	private void removeCoord(Chunk chunk, BlockPos pos)
	{
		chunks(chunk).remove(chunk, pos);
	}

	//#end updateCoordinates

	//#region Events
	/**
	 * Called when a {@link Chunk} is loaded on the server.<br>
	 * Reads the coordinates saved in the Chunk's NBT.
	 *
	 * @param event the event
	 */
	@SubscribeEvent
	public void onDataLoad(ChunkDataEvent.Load event)
	{
		if (event.getData().hasKey("chunkNotifier"))
			chunks(event.getChunk()).addAll(event.getChunk(), readLongArray(event.getData()));
	}

	/**
	 * Called when a {@link Chunk} is saved on the server.<br>
	 * Writes the coordinates to be saved in the Chunk's NBT.
	 *
	 * @param event the event
	 */
	@SubscribeEvent
	public void onDataSave(ChunkDataEvent.Save event)
	{
		Set<BlockPos> coords = chunks(event.getChunk()).get(event.getChunk());
		if (!coords.isEmpty())
			writeLongArray(event.getData(), coords);
	}

	/**
	 * Reads a long array from {@link NBTTagCompound}.<br>
	 * From IvNBTHelper.readNBTLongs()
	 *
	 * @author Ivorius
	 * @param compound the compound
	 * @return the long[]
	 */
	private Set<BlockPos> readLongArray(NBTTagCompound compound)
	{
		ByteBuf bytes = Unpooled.copiedBuffer(compound.getByteArray("chunkNotifier"));
		Set<BlockPos> set = Sets.newHashSet();
		for (int i = 0; i < bytes.capacity() / 8; i++)
			set.add(BlockPos.fromLong(bytes.readLong()));
		return set;
	}

	/**
	 * Write long array into {@link NBTTagCompound}.<br>
	 * From IvNBTHelper.writeNBTLongs()
	 *
	 * @author Ivorius
	 * @param compound the compound
	 * @param longs the longs
	 */
	private void writeLongArray(NBTTagCompound compound, Set<BlockPos> set)
	{
		ByteBuf bytes = Unpooled.buffer(set.size() * 8);
		set.forEach(p -> bytes.writeLong(p.toLong()));
		compound.setByteArray("chunkNotifier", bytes.array());
	}

	/**
	 * Called when a client requests a {@link Chunk} from the server only.<br>
	 * Sends the chunks coordinates to the client.
	 *
	 * @param event the event
	 */
	@SubscribeEvent
	public void onChunkWatched(ChunkWatchEvent.Watch event)
	{
		Chunk chunk = event.getPlayer().world.getChunkFromChunkCoords(event.getChunk().x, event.getChunk().z);
		Set<BlockPos> coords = chunks(chunk).get(chunk);
		if (!coords.isEmpty())
			ChunkBlockMessage.sendCoords(chunk, coords, event.getPlayer());
	}

	/**
	 * Client only.<br>
	 * Sets the coordinates for a chunk received by {@link ChunkBlockMessage}.
	 *
	 * @param chunkX the chunk x
	 * @param chunkZ the chunk z
	 * @param coords the coords
	 */
	public void setCoords(int chunkX, int chunkZ, Set<BlockPos> coords)
	{
		Chunk chunk = Utils.getClientWorld().getChunkFromChunkCoords(chunkX, chunkZ);
		chunks(chunk).addAll(chunk, coords);
	}

	//#end Events

	/**
	 * Gets the chunks inside distance from coordinates.
	 *
	 * @param world the world
	 * @param x the x
	 * @param z the z
	 * @param distance the size
	 * @return the chunks
	 */
	//TODO: move into Utils
	public static List<Chunk> getAffectedChunks(World world, int x, int z, int distance)
	{
		AxisAlignedBB aabb = new AxisAlignedBB(x - distance, 0, z - distance, x + distance + 1, 1, z + distance + 1);
		return getAffectedChunks(world, aabb);
	}

	/**
	 * Gets the chunks colliding with the specified {@link AxisAlignedBB}. q
	 *
	 * @param world the world
	 * @param aabbs the aabbs
	 * @return the affected chunks
	 */
	//TODO: move into Utils
	public static List<Chunk> getAffectedChunks(World world, AxisAlignedBB... aabbs)
	{
		if (ArrayUtils.isEmpty(aabbs))
			return ImmutableList.of();

		List<Chunk> chunks = new ArrayList<>();
		for (AxisAlignedBB aabb : aabbs)
		{
			if (aabb == null)
				continue;
			for (int cx = (int) Math.floor(aabb.minX) >> 4; cx <= (int) Math.ceil(aabb.maxX) >> 4; cx++)
			{
				for (int cz = (int) Math.floor(aabb.minZ) >> 4; cz <= (int) Math.ceil(aabb.maxZ) >> 4; cz++)
				{
					if (world.getChunkProvider() != null && world.getChunkProvider().getLoadedChunk(cx, cz) != null)
						chunks.add(world.getChunkFromChunkCoords(cx, cz));
				}
			}
		}
		return chunks;
	}

	/**
	 * Gets the {@link ChunkBlockHandler} instance.
	 *
	 * @return the chunk block handler
	 */
	public static ChunkBlockHandler get()
	{
		return instance;
	}

	/**
	 * This class is the base for a process that is to be called for every coordinate stored inside a {@link Chunk}.
	 */
	public static abstract class ChunkProcedure implements TLongProcedure
	{
		protected World world;
		protected Chunk chunk;
		protected MBlockState state;

		/**
		 * Sets the {@link Chunk} (and {@link World}) for this {@link ChunkProcedure}.
		 *
		 * @param chunk the chunk
		 */
		protected void set(Chunk chunk)
		{
			this.world = chunk.getWorld();
			this.chunk = chunk;
		}

		/**
		 * Checks whether the passed coordinate is a valid {@link IChunkBlock}.<br>
		 * Also sets the {@link MBlockState} for this {@link ChunkProcedure}.
		 *
		 * @param coord the coord
		 * @return true, if successful
		 */
		protected boolean check(long coord)
		{
			state = new MBlockState(world, coord);

			if (!(state.getBlock() instanceof IChunkBlock))
			{
				MalisisDoors.log.info(	"[ChunkNotificationHandler]  Removing invalid {} coordinate : {} in chunk {},{}",
										world.isRemote ? "client" : "server",
										state,
										chunk.x,
										chunk.z);
				get().removeCoord(chunk, state.getPos());
				return false;
			}

			return true;
		}

		/**
		 * Cleans the current state for this {@link ChunkProcedure} for the next coordinate.
		 */
		protected void clean()
		{
			world = null;
			chunk = null;
			state = null;
		}
	}

}
