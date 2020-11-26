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
import net.malisis.doors.block.component.DirectionalComponent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

/**
 * @author Ordinastie
 *
 */
public class EnumFacingUtils
{
	/**
	 * Gets the rotation count for the facing.
	 *
	 * @param facing the facing
	 * @return the rotation count
	 */
	public static int getRotationCount(EnumFacing facing)
	{
		if (facing == null)
			return 0;

		switch (facing)
		{
			case EAST:
				return 1;
			case NORTH:
				return 2;
			case WEST:
				return 3;
			case SOUTH:
			default:
				return 0;
		}
	}

	/**
	 * Gets the rotation count for the {@link IBlockState}
	 *
	 * @param state the state
	 * @return the rotation count
	 */
	public static int getRotationCount(IBlockState state)
	{
		EnumFacing direction = DirectionalComponent.getDirection(state);
		return EnumFacingUtils.getRotationCount(direction);
	}

	/**
	 * Rotates facing {@code count} times.
	 *
	 * @param facing the facing
	 * @param count the count
	 * @return the enum facing
	 */
	public static EnumFacing rotateFacing(EnumFacing facing, int count)
	{
		if (facing == null)
			return null;

		while (count-- > 0)
			facing = facing.rotateAround(EnumFacing.Axis.Y);
		return facing;
	}

	/**
	 * Gets the real side of a rotated block.
	 *
	 * @param state the state
	 * @param side the side
	 * @return the real side
	 */
	public static EnumFacing getRealSide(IBlockState state, EnumFacing side)
	{
		if (state == null || side == null)
			return side;

		EnumFacing direction = DirectionalComponent.getDirection(state);
		if (direction == EnumFacing.SOUTH)
			return side;

		if (direction == EnumFacing.DOWN)
			return side.rotateAround(Axis.X);
		else if (direction == EnumFacing.UP)
			switch (side)
			{
				case UP:
					return EnumFacing.SOUTH;
				case DOWN:
					return EnumFacing.NORTH;
				case NORTH:
					return EnumFacing.UP;
				case SOUTH:
					return EnumFacing.DOWN;
				default:
					return side;
			}

		int count = EnumFacingUtils.getRotationCount(direction);
		side = EnumFacingUtils.rotateFacing(side, count);

		return side;
	}
}
