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

package net.malisis.doors.inventory.cache;

import net.malisis.doors.inventory.MalisisSlot;
import net.malisis.doors.util.cacheddata.ICachedData;

/**
 * @author Ordinastie
 *
 */
public class CachedSlot implements ICachedData
{
	private MalisisSlot slot;
	private CachedItemStack cachedItemStack;
	private CachedItemStack cachedDraggedItemStack;

	public CachedSlot(MalisisSlot slot)
	{
		this.slot = slot;
		cachedItemStack = new CachedItemStack(() -> slot.getItemStack().copy());
		cachedDraggedItemStack = new CachedItemStack(slot::getItemStack);
	}

	public MalisisSlot getSlot()
	{
		return slot;
	}

	@Override
	public boolean hasChanged()
	{
		return cachedItemStack.hasChanged() || cachedDraggedItemStack.hasChanged();
	}

	@Override
	public void update()
	{
		cachedItemStack.update();
		cachedDraggedItemStack.update();
	}
}
