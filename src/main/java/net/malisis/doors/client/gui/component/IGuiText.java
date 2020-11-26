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

package net.malisis.doors.client.gui.component;

import net.malisis.doors.client.gui.MalisisGui;
import net.malisis.doors.renderer.font.FontOptions;
import net.malisis.doors.renderer.font.MalisisFont;

/**
 * Defines a {@link UIComponent} that uses text to be rendered on the {@link MalisisGui}.
 *
 * @author Ordinastie
 *
 */
public interface IGuiText<T>
{
	/**
	 * Gets the {@link MalisisFont}.
	 *
	 * @return the font
	 */
	public MalisisFont getFont();

	/**
	 * Sets the {@link MalisisFont}.
	 *
	 * @param font the new font
	 * @return the t
	 */
	public T setFont(MalisisFont font);

	/**
	 * Gets the {@link FontOptions}.
	 *
	 * @return the font options
	 */
	public FontOptions getFontOptions();

	/**
	 * Sets the {@link FontOptions}.
	 *
	 * @param fontOptions the font options
	 * @return the t
	 */
	public T setFontOptions(FontOptions fontOptions);
}
