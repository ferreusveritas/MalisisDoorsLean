/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 PaleoCrafter, Ordinastie
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

package net.malisis.doors.client.gui.component.interaction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.malisis.doors.client.gui.GuiRenderer;
import net.malisis.doors.client.gui.MalisisGui;
import net.malisis.doors.client.gui.component.IGuiText;
import net.malisis.doors.client.gui.component.UIComponent;
import net.malisis.doors.client.gui.element.SimpleGuiShape;
import net.malisis.doors.client.gui.event.ComponentEvent.ValueChange;
import net.malisis.doors.renderer.font.FontOptions;
import net.malisis.doors.renderer.font.MalisisFont;
import net.malisis.doors.renderer.icon.provider.GuiIconProvider;
import net.minecraft.client.renderer.OpenGlHelper;

/**
 * UICheckBox
 *
 * @author PaleoCrafter
 */
public class UICheckBox extends UIComponent<UICheckBox> implements IGuiText<UICheckBox>
{
	/** The {@link MalisisFont} to use for this {@link UICheckBox}. */
	protected MalisisFont font = MalisisFont.minecraftFont;
	/** The {@link FontOptions} to use for this {@link UICheckBox}. */
	protected FontOptions fontOptions = FontOptions.builder().color(0x444444).build();
	/** Text to draw beside the checkbox. **/
	private String text;
	/** Whether this {@link UICheckBox} is checked. */
	private boolean checked;

	private GuiIconProvider cbIconProvider;

	public UICheckBox(MalisisGui gui, String text)
	{
		super(gui);
		setText(text);

		shape = new SimpleGuiShape();

		iconProvider = new GuiIconProvider(	gui.getGuiTexture().getIcon(242, 32, 10, 10),
											null,
											gui.getGuiTexture().getIcon(252, 32, 10, 10));
		cbIconProvider = new GuiIconProvider(	gui.getGuiTexture().getIcon(242, 52, 12, 10),
												gui.getGuiTexture().getIcon(254, 42, 12, 10),
												gui.getGuiTexture().getIcon(242, 42, 12, 10));
	}

	public UICheckBox(MalisisGui gui)
	{
		this(gui, null);
	}

	//#region Getters/Setters
	@Override
	public MalisisFont getFont()
	{
		return font;
	}

	@Override
	public UICheckBox setFont(MalisisFont font)
	{
		this.font = font;
		calculateSize();
		return this;
	}

	@Override
	public FontOptions getFontOptions()
	{
		return fontOptions;
	}

	@Override
	public UICheckBox setFontOptions(FontOptions options)
	{
		this.fontOptions = options;
		calculateSize();
		return this;
	}

	/**
	 * Sets the text for this {@link UICheckBox}.
	 *
	 * @param text the new text
	 */
	public UICheckBox setText(String text)
	{
		this.text = text;
		calculateSize();
		return this;
	}

	/**
	 * Gets the text for this {@link UICheckBox}.
	 *
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	//#end Getters/Setters

	/**
	 * Calculates the size for this {@link UICheckBox}.
	 */
	private void calculateSize()
	{
		int w = StringUtils.isEmpty(text) ? 0 : (int) font.getStringWidth(text, fontOptions);
		setSize(w + 11, 10);
	}

	/**
	 * Checks if this {@link UICheckBox} is checked.
	 *
	 * @return whether this {@link UICheckBox} is checked or not.
	 */
	public boolean isChecked()
	{
		return this.checked;
	}

	/**
	 * Sets the state for this {@link UICheckBox}. Does not fire {@link CheckEvent}.
	 *
	 * @param checked true if checked
	 * @return this {@link UIComponent}
	 */
	public UICheckBox setChecked(boolean checked)
	{
		this.checked = checked;
		return this;
	}

	@Override
	public boolean onClick(int x, int y)
	{
		if (fireEvent(new CheckEvent(this, !checked)))
			checked = !checked;
		return true;
	}

	@Override
	public boolean onKeyTyped(char keyChar, int keyCode)
	{
		if (!isFocused())
			return super.onKeyTyped(keyChar, keyCode);

		if (keyCode == Keyboard.KEY_SPACE)
		{
			if (fireEvent(new CheckEvent(this, !checked)))
				checked = !checked;
		}

		return false;
	}

	@Override
	public void drawBackground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		shape.resetState();
		shape.setSize(10, 10);
		shape.setPosition(1, 0);
		renderer.drawShape(shape, rp);

		renderer.next();

		// draw the white shade over the slot
		if (hovered)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			rp.colorMultiplier.set(0xFFFFFF);
			rp.alpha.set(80);
			rp.useTexture.set(false);

			shape.resetState();
			shape.setSize(8, 8);
			shape.setPosition(2, 1);
			renderer.drawShape(shape, rp);
			renderer.next();

			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		if (!StringUtils.isEmpty(text))
			renderer.drawText(font, text, 14, 2, 0, fontOptions);
	}

	@Override
	public void drawForeground(GuiRenderer renderer, int mouseX, int mouseY, float partialTick)
	{
		if (checked)
		{
			if (isHovered() && isEnabled())
				GL11.glEnable(GL11.GL_BLEND);
			rp.reset();
			shape.resetState();
			shape.setSize(12, 10);
			rp.iconProvider.set(cbIconProvider);
			renderer.drawShape(shape, rp);
			renderer.next();
			if (isHovered() && isEnabled())
				GL11.glDisable(GL11.GL_BLEND);
		}
	}

	@Override
	public String getPropertyString()
	{
		return "text=" + text + " | checked=" + this.checked + " | " + super.getPropertyString();
	}

	/**
	 * Event fired when a {@link UICheckBox} is checked or unchecked.<br>
	 * When catching the event, the state is not applied to the {@code UICheckbox} yet.<br>
	 * Cancelling the event will prevent the state to be set for the {@code UICheckbox} .
	 */
	public static class CheckEvent extends ValueChange<UICheckBox, Boolean>
	{
		public CheckEvent(UICheckBox component, boolean checked)
		{
			super(component, component.isChecked(), checked);
		}

		/**
		 * @return the new state for the checkbox
		 */
		public boolean isChecked()
		{
			return newValue;
		}
	}

}
