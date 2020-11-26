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

import static net.malisis.doors.block.Door.*;
import net.malisis.doors.block.BoundingBoxType;
import net.malisis.doors.renderer.RenderParameters;
import net.malisis.doors.renderer.animation.Animation;
import net.malisis.doors.renderer.animation.transformation.Rotation;
import net.malisis.doors.renderer.model.MalisisModel;
import net.malisis.doors.util.AABBUtils;
import net.malisis.doors.DoorState;
import net.malisis.doors.tileentity.DoorTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Ordinastie
 *
 */
public class RotatingDoorMovement implements IDoorMovement
{
	@Override
	public AxisAlignedBB getOpenBoundingBox(DoorTileEntity tileEntity, boolean topBlock, BoundingBoxType type)
	{
		return AABBUtils.rotate(IDoorMovement.getFullBoundingBox(topBlock, type), tileEntity.isHingeLeft() ? -1 : 1);
	}

	private Rotation getTransformation(DoorTileEntity tileEntity)
	{
		float angle = -90;
		float hingeX = -0.5F + DOOR_WIDTH / 2;

		if (tileEntity.isHingeLeft())
		{
			hingeX = -hingeX;
			angle = -angle;
		}

		Rotation rotation = new Rotation(angle);
		rotation.aroundAxis(0, 1, 0).offset(hingeX, 0, -0.5F + DOOR_WIDTH / 2);
		rotation.reversed(tileEntity.getState() == DoorState.CLOSING || tileEntity.getState() == DoorState.CLOSED);
		rotation.forTicks(tileEntity.getDescriptor().getOpeningTime());

		return rotation;
	}

	@Override
	public Animation<?>[] getAnimations(DoorTileEntity tileEntity, MalisisModel model, RenderParameters rp)
	{
		return new Animation[] { new Animation<>(model, getTransformation(tileEntity)) };
	}

	@Override
	public boolean isSpecial()
	{
		return false;
	}
}
