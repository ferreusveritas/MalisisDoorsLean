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

package net.malisis.doors.client.gui.element;

import net.malisis.doors.renderer.element.Face;
import net.malisis.doors.renderer.element.Shape;
import net.malisis.doors.renderer.element.vertex.BottomSouthEast;
import net.malisis.doors.renderer.element.vertex.BottomSouthWest;
import net.malisis.doors.renderer.element.vertex.TopSouthEast;
import net.malisis.doors.renderer.element.vertex.TopSouthWest;

/**
 * @author Ordinastie
 *
 */
public abstract class GuiShape extends Shape
{
	public GuiShape(Face... faces)
	{
		super(faces);
	}

	public GuiShape(int faceCount)
	{
		faces = new Face[faceCount];
		for (int i = 0; i < faceCount; i++)
			faces[i] = new GuiFace();
	}

	public void setPosition(int x, int y)
	{
		translate(x, y, 0);
	}

	@Override
	public void translate(float x, float y, float z)
	{
		super.translate(x, y, z);
		applyMatrix();
	}

	public void translate(int x, int y)
	{
		translate(x, y, 0);
	}

	@Override
	public void rotate(float angle, float x, float y, float z)
	{
		rotate(angle, 0, 0, 1, x, y, z);
	}

	public void rotate(float angle)
	{
		//		rotate(angle, x + (x + width) / 2, y + (y + height) / 2, 0);
		//		applyMatrix();
	}

	@Override
	public void scale(float scale)
	{
		scale(scale, scale);
	}

	public abstract void setSize(int width, int height);

	public abstract void scale(float x, float y);

	public static class GuiFace extends Face
	{
		public GuiFace()
		{
			super(new BottomSouthWest().setBaseName("TopLeft"), new TopSouthWest().setBaseName("BottomLeft"), new TopSouthEast()
					.setBaseName("BottomRight"), new BottomSouthEast().setBaseName("TopRight"));
			setStandardUV();
		}

		public GuiFace(int width, int height)
		{
			this();
			scale(width, height, 0);
		}
	}
}
