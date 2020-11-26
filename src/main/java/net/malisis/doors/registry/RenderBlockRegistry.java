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

package net.malisis.doors.registry;

import net.malisis.doors.registry.RenderBlockRegistry.IRenderBlockCallback;
import net.malisis.doors.registry.RenderBlockRegistry.IRenderBlockCallbackPredicate;
import net.malisis.doors.util.callback.CallbackRegistry;
import net.malisis.doors.util.callback.CallbackResult;
import net.malisis.doors.util.callback.ICallback;
import net.malisis.doors.util.callback.ICallback.ICallbackPredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author Ordinastie
 *
 */
public class RenderBlockRegistry extends CallbackRegistry<IRenderBlockCallback, IRenderBlockCallbackPredicate, Boolean>
{
	/**
	 * Specialized {@link ICallback} called when a block is rendered.
	 */
	public interface IRenderBlockCallback extends ICallback<Boolean>
	{
		@Override
		public default CallbackResult<Boolean> call(Object... params)
		{
			return callback((BufferBuilder) params[0], (IBlockAccess) params[1], (BlockPos) params[2], (IBlockState) params[3]);
		}

		public CallbackResult<Boolean> callback(BufferBuilder buffer, IBlockAccess world, BlockPos pos, IBlockState state);
	}

	/**
	 * Specialized {@link ICallbackPredicate} for {@link IRenderBlockCallback IRenderBlockCallbacks}
	 */
	public interface IRenderBlockCallbackPredicate extends ICallbackPredicate
	{
		@Override
		public default boolean apply(Object... params)
		{
			return apply((BufferBuilder) params[0], (IBlockAccess) params[1], (BlockPos) params[2], (IBlockState) params[3]);
		}

		public boolean apply(BufferBuilder buffer, IBlockAccess world, BlockPos pos, IBlockState state);
	}
}
