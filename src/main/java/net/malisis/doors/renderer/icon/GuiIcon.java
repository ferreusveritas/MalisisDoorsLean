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

package net.malisis.doors.renderer.icon;

import java.util.Arrays;

/**
 * @author Ordinastie
 *
 */
public class GuiIcon extends Icon
{
	protected Icon[] icons;

	public GuiIcon(Icon icon)
	{
		this.icons = new Icon[] { icon };
	}

	public GuiIcon(Icon[] icons)
	{
		this.icons = icons;
	}

	public Icon getIcon(int index)
	{
		if (icons == null || icons.length == 0)
			return null;

		//make sure we don't overflow
		index = index % icons.length;

		if (icons.length < 3)
			return icons[index];

		int row = index / 3;
		int col = index % 3;

		if (flippedU)
			index = 3 * row + (2 - col);

		row = index / 3;
		col = index % 3;

		if (flippedV)
			index = 3 * (2 - row) + col;

		return icons[index];
	}

	@Override
	public Icon flip(boolean horizontal, boolean vertical)
	{
		for (Icon icon : icons)
			icon.flip(horizontal, vertical);
		return super.flip(horizontal, vertical);
	}

	@Override
	public void setRotation(int rotation)
	{
		for (Icon icon : icons)
			icon.setRotation(rotation);
		super.setRotation(rotation);
	}

	@Override
	public Icon clip(float offsetXFactor, float offsetYFactor, float widthFactor, float heightFactor)
	{
		for (Icon icon : icons)
			icon.clip(offsetXFactor, offsetYFactor, widthFactor, heightFactor);
		return super.clip(offsetXFactor, offsetYFactor, widthFactor, heightFactor);
	}

	@Override
	public Icon clip(int offsetX, int offsetY, int width, int height)
	{
		for (Icon icon : icons)
			icon.clip(offsetX, offsetY, width, height);
		return super.clip(offsetX, offsetY, width, height);
	}

	@Override
	public String toString()
	{
		return Arrays.toString(icons);
	}
}
