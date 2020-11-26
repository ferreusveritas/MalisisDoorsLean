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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.registry.MalisisRegistry;
import net.malisis.doors.util.callback.CallbackResult;
import net.malisis.doors.util.callback.ICallback.CallbackOption;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Extension of {@link TextureAtlasSprite} to allow common operations like clipping and offset.<br>
 * Icons should be acquired by using the {@link #from(String)} method so that multiple call with the same name return the same {@link Icon}
 * instance.<br>
 * For non registered {@link Icon}, use constructors directly.
 *
 * @author Ordinastie
 *
 */
@AutoLoad
@SideOnly(Side.CLIENT) //for @Autoload
public class Icon extends TextureAtlasSprite
{
	/** Map of all registered {@link Icon}. These icons will be stitched with the {@link TextureStitchEvent}. */
	protected final static Map<String, Icon> registeredIcons = Maps.newHashMap();
	/** Map of all registered {@link VanillaIcon}. These icons will be updated after the {@link TextureStitchEvent}. */
	protected final static Map<Object, VanillaIcon> vanillaIcons = Maps.newHashMap();

	static
	{
		MalisisRegistry.onTextureStitched(Icon::registerIcons, CallbackOption.of());
	}

	/** {@link Icon} version of the missing texture **/
	public static Icon missing = new ProxyIcon("MISSINGNO")
	{
		@Override
		public TextureAtlasSprite getIcon()
		{
			return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}

		@Override
		public String toString()
		{
			return "MISSINGNO";
		}
	};

	/** Width of the block's texture atlas. */
	public static int BLOCK_TEXTURE_WIDTH = -1;
	/** Height of the block's texture atlas. */
	public static int BLOCK_TEXTURE_HEIGHT = -1;

	/** Width of the global texture sheet. */
	protected int sheetWidth;
	/** Height of the global texture sheet. */
	protected int sheetHeight;

	/** Is the icon flipped on the horizontal axis. */
	protected boolean flippedU = false;
	/** Is the icon flipped on the vertical axis. */
	protected boolean flippedV = false;
	/** Rotation value (clockwise). */
	protected int rotation = 0;

	/** Lists of Icon depending on this one. */
	protected Set<Icon> dependants = new HashSet<>();

	/**
	 * Instantiates a new {@link Icon}.
	 *
	 * @param name the name
	 */
	public Icon(String name)
	{
		super(name);
		maxU = 1;
		maxV = 1;
	}

	/**
	 * Instantiates a new {@link Icon}.
	 */
	public Icon()
	{
		this("");
	}

	/**
	 * Instantiates a new {@link Icon}.
	 *
	 * @param name the name
	 * @param u the u
	 * @param v the v
	 * @param U the u
	 * @param V the v
	 */
	public Icon(String name, float u, float v, float U, float V)
	{
		this(name);
		minU = u;
		minV = v;
		maxU = U;
		maxV = V;
	}

	public Icon(TextureAtlasSprite icon)
	{
		this(icon.getIconName());
		copyFrom(icon);
	}

	//#region getters/setters
	/**
	 * Sets the size in pixel of this {@link Icon}.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setSize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets the u vs.
	 *
	 * @param u the u
	 * @param v the v
	 * @param U the u
	 * @param V the v
	 */
	public void setUVs(float u, float v, float U, float V)
	{
		minU = u;
		minV = v;
		maxU = U;
		maxV = V;
	}

	/**
	 * Gets the min u.
	 *
	 * @return the min u
	 */
	@Override
	public float getMinU()
	{
		return this.flippedU ? maxU : minU;
	}

	/**
	 * Gets the max u.
	 *
	 * @return the max u
	 */
	@Override
	public float getMaxU()
	{
		return this.flippedU ? minU : maxU;
	}

	/**
	 * Gets the min v.
	 *
	 * @return the min v
	 */
	@Override
	public float getMinV()
	{
		return this.flippedV ? maxV : minV;
	}

	/**
	 * Gets the max v.
	 *
	 * @return the max v
	 */
	@Override
	public float getMaxV()
	{
		return this.flippedV ? minV : maxV;
	}

	/**
	 * Sets this {@link Icon} to be flipped.
	 *
	 * @param horizontal whether to flip horizontally
	 * @param vertical whether to flip vertically
	 * @return this {@link Icon}
	 */
	public Icon flip(boolean horizontal, boolean vertical)
	{
		flippedU = horizontal;
		flippedV = vertical;
		return this;
	}

	/**
	 * Checks if is flipped u.
	 *
	 * @return true if this {@link Icon} is flipped horizontally.
	 */
	public boolean isFlippedU()
	{
		return flippedU;
	}

	/**
	 * Checks if is flipped v.
	 *
	 * @return true if this {@link Icon} is flipped vertically.
	 */
	public boolean isFlippedV()
	{
		return flippedV;
	}

	/**
	 * Checks if is rotated.
	 *
	 * @return true fi this {@link Icon} is rotated.
	 */
	public boolean isRotated()
	{
		return rotation != 0;
	}

	/**
	 * Sets the rotation for this {@link Icon}. The icon will be rotated <b>rotation</b> x 90 degrees clockwise.
	 *
	 * @param rotation the rotation
	 */
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}

	/**
	 * Gets the rotation.
	 *
	 * @return the rotation for this {@link Icon}.
	 */
	public int getRotation()
	{
		return rotation;
	}

	//#end getters/setters

	/**
	 * Initializes this {@link Icon}. Called from the icon this one depends on, copying the <b>baseIcon</b> values.
	 *
	 * @param baseIcon the base icon
	 * @param width the width
	 * @param height the height
	 * @param x the x
	 * @param y the y
	 * @param rotated the rotated
	 */
	protected void initIcon(Icon baseIcon, int width, int height, int x, int y, boolean rotated)
	{
		copyFrom(baseIcon);
	}

	/**
	 * Offsets this {@link Icon} by a specified amount. <b>offsetX</b> and <b>offsetY</b> are specified in pixels.
	 *
	 * @param offsetX the x offset
	 * @param offsetY the y offset
	 * @return this {@link Icon}
	 */
	public Icon offset(int offsetX, int offsetY)
	{
		initSprite(sheetWidth, sheetHeight, getOriginX() + offsetX, getOriginY() + offsetY, isRotated());
		return this;
	}

	/**
	 * Clips this {@link Icon}. <b>offsetX</b>, <b>offsetY</b>, <b>width</b> and <b>height</b> are specified in pixels.
	 *
	 * @param offsetX the x offset
	 * @param offsetY the y offset
	 * @param width the width
	 * @param height the height
	 * @return this {@link Icon}
	 */
	public Icon clip(int offsetX, int offsetY, int width, int height)
	{
		this.width = width;
		this.height = height;
		offset(offsetX, offsetY);

		return this;
	}

	/**
	 * Clips this {@link Icon}. <b>offsetXFactor</b>, <b>offsetYFactor</b>, <b>widthFactor</b> and <b>heightFactor</b> are values from zero
	 * to one.
	 *
	 * @param offsetXFactor the x factor for offset
	 * @param offsetYFactor the y factor for offset
	 * @param widthFactor the width factor
	 * @param heightFactor the height factor
	 * @return this {@link Icon}
	 */
	public Icon clip(float offsetXFactor, float offsetYFactor, float widthFactor, float heightFactor)
	{
		int offsetX = Math.round(width * offsetXFactor);
		int offsetY = Math.round(height * offsetYFactor);

		width = Math.round(width * widthFactor);
		height = Math.round(height * heightFactor);

		offset(offsetX, offsetY);

		return this;
	}

	/**
	 * Called when the part represented by this {@link Icon} is stiched to the texture. Sets most of the icon fields.
	 *
	 * @param width the width
	 * @param height the height
	 * @param x the x
	 * @param y the y
	 * @param rotated the rotated
	 */
	@Override
	public void initSprite(int width, int height, int x, int y, boolean rotated)
	{
		if (width == 0 || height == 0)
		{
			//assume block atlas
			width = BLOCK_TEXTURE_WIDTH;
			height = BLOCK_TEXTURE_HEIGHT;
		}
		this.sheetWidth = width;
		this.sheetHeight = height;
		super.initSprite(width, height, x, y, rotated);
		for (TextureAtlasSprite dep : dependants)
		{
			if (dep instanceof Icon)
				((Icon) dep).initIcon(this, width, height, x, y, rotated);
			else
				dep.copyFrom(this);
		}
	}

	/**
	 * Copies the values from {@link Icon base} to this {@link Icon}.
	 *
	 * @param base the icon to copy from
	 */
	@Override
	public void copyFrom(TextureAtlasSprite base)
	{
		this.originX = base.getOriginX();
		this.originY = base.getOriginY();
		this.width = base.getIconWidth();
		this.height = base.getIconHeight();
		this.minU = base.getMinU();
		this.maxU = base.getMaxU();
		this.minV = base.getMinV();
		this.maxV = base.getMaxV();

		for (int i = 0; i < base.getFrameCount(); i++)
			this.framesTextureData.add(base.getFrameTextureData(i));

		if (base instanceof Icon)
		{
			Icon mbase = (Icon) base;
			this.sheetWidth = mbase.sheetWidth;
			this.sheetHeight = mbase.sheetHeight;
			this.flippedU = mbase.flippedU;
			this.flippedV = mbase.flippedV;
		}
	}

	/**
	 * Creates a new {@link Icon} from this <code>Icon</code>.
	 *
	 * @return the new {@link Icon}
	 */
	public Icon copy()
	{
		return new Icon(this);
	}

	public void register(TextureMap map)
	{
		map.setTextureEntry(this);
	}

	/**
	 * Registers all the {@link Icon} into the {@link TextureMap}.
	 *
	 * @param map the map
	 */
	public static CallbackResult<Void> registerIcons(TextureMap map)
	{
		registeredIcons.values().forEach(icon -> icon.register(map));
		vanillaIcons.values().forEach(icon -> icon.register(map));
		return CallbackResult.noResult();
	}

	/**
	 * Gets a {@link Icon} with the specified name.<br>
	 * This method ensures the same instance is return when called multiple time with the same string.
	 *
	 * @param name the name
	 * @return the malisis icon
	 */
	public static Icon from(String name)
	{
		if (registeredIcons.get(name) != null)
			return registeredIcons.get(name);

		Icon icon = null;
		if (name.indexOf("minecraft:") == 0)
			icon = new VanillaIcon(name);
		else
			icon = new Icon(name);
		registeredIcons.put(name, icon);
		return icon;
	}

	/**
	 * Gets a {@link Icon} for the texture used for the {@link Block} default {@link IBlockState}.
	 *
	 * @param block the block
	 * @return the malisis icon
	 */
	public static Icon from(Block block)
	{
		return from(block.getDefaultState());
	}

	/**
	 * Gets a {@link Icon} for the texture used for the {@link IBlockState}
	 *
	 * @param state the state
	 * @return the malisis icon
	 */
	public static Icon from(IBlockState state)
	{
		if (vanillaIcons.get(state) != null)
			return vanillaIcons.get(state);

		VanillaIcon icon = new VanillaIcon(state);
		vanillaIcons.put(state, icon);
		return icon;
	}

	/**
	 * Gets a {@link Icon} for the texture used for the {@link Item}
	 *
	 * @param item the item
	 * @return the malisis icon
	 */
	public static Icon from(Item item)
	{
		return from(item, 0);
	}

	/**
	 * Gets a {@link Icon} for the texture used for the {@link Item}
	 *
	 * @param item the item
	 * @return the malisis icon
	 */
	public static Icon from(Item item, int metadata)
	{
		Pair<Item, Integer> p = Pair.of(item, metadata);
		if (vanillaIcons.get(p) != null)
			return vanillaIcons.get(p);

		VanillaIcon icon = new VanillaIcon(item, metadata);
		vanillaIcons.put(p, icon);
		return icon;
	}

	/**
	 * Gets the {@link Icon} registered with specified name.
	 *
	 * @param name the name
	 * @return the registered
	 */
	public static Icon getRegistered(String name)
	{
		return registeredIcons.get(name);
	}
}
