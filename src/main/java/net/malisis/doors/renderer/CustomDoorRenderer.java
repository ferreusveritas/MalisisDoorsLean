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

package net.malisis.doors.renderer;

import org.apache.commons.lang3.tuple.Triple;

import net.malisis.doors.renderer.element.Shape;
import net.malisis.doors.renderer.element.face.BottomFace;
import net.malisis.doors.renderer.element.face.NorthFace;
import net.malisis.doors.renderer.element.face.SouthFace;
import net.malisis.doors.renderer.element.face.TopFace;
import net.malisis.doors.renderer.element.shape.Cube;
import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.renderer.model.MalisisModel;
import net.malisis.doors.block.CustomDoor;
import net.malisis.doors.block.Door;
import net.malisis.doors.item.CustomDoorItem;
import net.malisis.doors.tileentity.CustomDoorTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

/**
 * @author Ordinastie
 *
 */

public class CustomDoorRenderer extends DoorRenderer
{
	public static CustomDoorRenderer instance = new CustomDoorRenderer();
	private IBlockState frame;
	private IBlockState top;
	private IBlockState bottom;

	private float width;

	public CustomDoorRenderer()
	{
		super(false);
		registerFor(CustomDoorTileEntity.class);
	}

	@Override
	protected void initialize()
	{
		//TODO: make an OBJ model
		width = 1.0F / 8.0F;
		/**
		 * BOTTOM
		 */
		//frame right
		Shape frameR = new Cube().setSize(width, 1, Door.DOOR_WIDTH);
		//frame left
		Shape frameL = new Shape(frameR);
		frameL.translate(1 - width, 0, 0);
		//frame horizontal
		Shape frameH = new Shape(new NorthFace(), new SouthFace(), new TopFace(), new BottomFace());
		frameH.setSize(1 - 2 * width, width, Door.DOOR_WIDTH);
		frameH.translate(width, 0, 0);

		//full bottom frame
		Shape frame = Shape.fromShapes(frameR, frameL, frameH);
		frame.scale(1, 1, 0.995F); //scale frame to prevent z-fighting when slid in walls
		frame.applyMatrix();

		//bottom material
		Shape mat = new Shape(new SouthFace(), new NorthFace(), new TopFace());
		mat.setSize(1 - 2 * width, 1 - width, Door.DOOR_WIDTH * 0.6F).translate(width, width, Door.DOOR_WIDTH * 0.2F);
		mat.applyMatrix();

		Shape bottom = new Shape();
		bottom.addFaces(frame.getFaces(), "frame");
		bottom.addFaces(mat.getFaces(), "material");
		bottom.interpolateUV();
		bottom.storeState();

		/**
		 * TOP
		 */
		frameR = new Shape(frameR);
		frameL = new Shape(frameL);
		frameH = new Shape(frameH);
		frameH.translate(0, 1 - width, 0);

		//full top frame
		frame = Shape.fromShapes(frameR, frameL, frameH);
		frame.scale(1, 1, 0.995F); //scale frame to prevent z-fighting when slid in walls
		frame.applyMatrix();

		//top material
		mat = new Shape(mat);
		mat.translate(0, -width, 0);
		mat.applyMatrix();

		Shape top = new Shape();
		top.addFaces(frame.getFaces(), "frame");
		top.addFaces(mat.getFaces(), "material");
		top.translate(0, 1, 0);
		top.interpolateUV();
		top.storeState();

		//store top and bottom inside a model
		model = new MalisisModel();
		model.addShape("bottom", bottom);
		model.addShape("top", top);
		model.storeState();

		initParams();

		ensureBlock(CustomDoor.class);
	}

	@Override
	public boolean isGui3d()
	{
		return true;
	}

	@Override
	protected void setItem()
	{
		Triple<IBlockState, IBlockState, IBlockState> triple = CustomDoorItem.readNBT(itemStack.getTagCompound());
		frame = triple.getLeft();
		top = triple.getMiddle();
		bottom = triple.getRight();
		direction = EnumFacing.SOUTH;

		setupParams();
	}

	@Override
	protected void setTileEntity()
	{
		super.setTileEntity();
		CustomDoorTileEntity tileEntity = (CustomDoorTileEntity) this.tileEntity;

		frame = tileEntity.getFrame();
		top = tileEntity.getTop();
		bottom = tileEntity.getBottom();

		setupParams();
	}

	private void setupParams()
	{
		//reset alpha before so it doesn't bleed to the shapes
		rp.alpha.reset();

		rp.icon.set(Icon.from(frame));
		rp.colorMultiplier.set(getColor(frame));
		model.getShape("top").setParameters("frame", rp, true);
		model.getShape("bottom").setParameters("frame", rp, true);

		rp.icon.set(Icon.from(top));
		rp.colorMultiplier.set(getColor(top));
		model.getShape("top").setParameters("material", rp, true);

		rp.icon.set(Icon.from(bottom));
		rp.colorMultiplier.set(getColor(bottom));
		model.getShape("bottom").setParameters("material", rp, true);

		//reset the values to default as rp is used for the whole shape
		rp.icon.reset();
		rp.colorMultiplier.reset();
	}

	private int getColor(IBlockState state)
	{
		if (state.getBlock() == Blocks.GRASS)
			return 0xFFFFFF;
		return colorMultiplier(world, pos, state);
	}
}
