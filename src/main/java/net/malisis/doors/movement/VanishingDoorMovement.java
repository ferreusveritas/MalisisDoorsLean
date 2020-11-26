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
import net.malisis.doors.renderer.animation.transformation.AlphaTransform;
import net.malisis.doors.renderer.model.MalisisModel;
import net.malisis.doors.DoorState;
import net.malisis.doors.tileentity.DoorTileEntity;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Ordinastie
 *
 */
public class VanishingDoorMovement implements IDoorMovement
{

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
		AlphaTransform alpha = new AlphaTransform(255, 0);
		alpha.reversed(tileEntity.getState() == DoorState.CLOSING || tileEntity.getState() == DoorState.CLOSED);
		alpha.forTicks(tileEntity.getDescriptor().getOpeningTime());
		return new Animation[] { new Animation<>(rp, alpha) };
	}

	@Override
	public boolean isSpecial()
	{
		return false;
	}
}
