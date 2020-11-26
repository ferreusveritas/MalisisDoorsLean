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

package net.malisis.doors.movement;

import net.malisis.doors.block.BoundingBoxType;
import net.malisis.doors.renderer.RenderParameters;
import net.malisis.doors.renderer.animation.Animation;
import net.malisis.doors.renderer.animation.transformation.ParallelTransformation;
import net.malisis.doors.renderer.animation.transformation.Rotation;
import net.malisis.doors.renderer.animation.transformation.Scale;
import net.malisis.doors.renderer.model.MalisisModel;
import net.malisis.doors.DoorState;
import net.malisis.doors.tileentity.DoorTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Ordinastie
 *
 */
public class SpinningDoorMovement implements IDoorMovement
{
	private Rotation rotBot = new Rotation(0).aroundAxis(0, 0, 1);
	private Rotation rotTop = new Rotation(0).aroundAxis(0, 0, 1).offset(0, 1, 0);
	private Scale scaleBot = new Scale(0, 0, 0);
	private Scale scaleTop = new Scale(0, 0, 0).offset(0, 1, 0);

	@Override
	public AxisAlignedBB getOpenBoundingBox(DoorTileEntity tileEntity, boolean topBlock, BoundingBoxType type)
	{
		if (type == BoundingBoxType.COLLISION)
			return null;

		return IDoorMovement.getFullBoundingBox(topBlock, type);
	}

	@Override
	public Animation<?>[] getAnimations(DoorTileEntity tileEntity, MalisisModel model, RenderParameters rp)
	{
		float angle = tileEntity.isHingeLeft() ? 720 : -720;
		boolean closed = tileEntity.getState() == DoorState.CLOSING || tileEntity.getState() == DoorState.CLOSED;
		int ot = tileEntity.getDescriptor().getOpeningTime();

		rotBot.from(angle);
		rotBot.reversed(closed);
		rotBot.forTicks(ot);

		rotTop.from(angle);
		rotTop.reversed(closed);
		rotTop.forTicks(ot);

		scaleBot.reversed(closed);
		scaleBot.forTicks(ot);

		scaleTop.reversed(closed);
		scaleTop.forTicks(ot);

		ParallelTransformation bot = new ParallelTransformation(rotBot, scaleBot);
		ParallelTransformation top = new ParallelTransformation(rotTop, scaleTop);

		return new Animation[] { new Animation<>(model.getShape("bottom"), bot), new Animation<>(model.getShape("top"), top) };
	}

	@Override
	public boolean isSpecial()
	{
		return false;
	}
}
