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

import java.util.LinkedList;
import java.util.List;

import net.malisis.doors.renderer.animation.transformation.ITransformable;
import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * @author Ordinastie
 *
 */
public class RenderParameters implements ITransformable.Color, ITransformable.Alpha, ITransformable.Brightness, Cloneable
{
	/** List of parameters inside this {@link RenderParameters}. */
	protected List<Parameter<?>> listParams;

	/** Defines whether to render all faces even if shoudSideBeRendered is false */
	public Parameter<Boolean> renderAllFaces = new Parameter<>(false);

	/** Defines whether to use the block bounding box instead of renderBounds (Block Level) */
	public Parameter<Boolean> useBlockBounds = new Parameter<>(true);

	/** Defines the rendering bounds to limit the vertex inside (Block Level) */
	public Parameter<AxisAlignedBB> renderBounds = new Parameter<>(null);

	/** Define whether a custom texture for drawing. It disable default icon behavior. A ResourceLocation need to be bound. */
	public Parameter<Boolean> useCustomTexture = new Parameter<>(false);

	/** Define whether to apply texture UV for the shape (Block level) */
	public Parameter<Boolean> applyTexture = new Parameter<>(true);

	/** Defines an {@link IIconProvider} to be used to get the icon for the faces. (Block Level) */
	public Parameter<IIconProvider> iconProvider = new Parameter<>(null);

	/** Defines a {@link Icon} to be used for the rendering. */
	public Parameter<Icon> icon = new Parameter<>(null);

	/** Defines whether to use block.getIcon(world, x, y, z, side) instead of block.getIcon(side, metadata) to get the IIcon */
	public Parameter<Boolean> useWorldSensitiveIcon = new Parameter<>(true);

	/** Defines whether to use a texture (will call addVertexWithUV instead of addVertex) */
	public Parameter<Boolean> useTexture = new Parameter<>(true);

	/** Defines whether to calculate interpolated textures coordinates depending on block bounds (Block Level) */
	public Parameter<Boolean> interpolateUV = new Parameter<>(true);

	/** Defines whether to automatically rotate the icons for {@link DirectionalComponent} */
	public Parameter<Boolean> rotateIcon = new Parameter<>(true);

	/** Defines whether to calculate ambient occlusion color or not (Block Level) */
	public Parameter<Boolean> calculateAOColor = new Parameter<>(true);

	/** Defines whether to calculate brightness or not (Block Level) */
	public Parameter<Boolean> calculateBrightness = new Parameter<>(true);

	/** Define whether to override each vertex color. If false, FaceParams.colorMultiplier will be used instead (Block Level) */
	public Parameter<Boolean> usePerVertexColor = new Parameter<>(false);

	/** Define whether to override each vertex alpha. If false, FaceParams.alpha will be used instead (Block Level) */
	public Parameter<Boolean> usePerVertexAlpha = new Parameter<>(false);

	/** Define whether to override each vertex brightness. If false, FaceParams.brightness will be used instead (Block Level) */
	public Parameter<Boolean> usePerVertexBrightness = new Parameter<>(false);

	/** Defines whether to use the block mixBlockBrightness (if false, RenderParameters.brightness will be used) (Block Level) */
	public Parameter<Boolean> useEnvironmentBrightness = new Parameter<>(true);

	/** Defines whether to use the defined normals */
	public Parameter<Boolean> useNormals = new Parameter<>(false);

	/**
	 * Defines the color to apply to the face (useful for grass and leaves) usePerVertexColor must be false (Block Level).<br>
	 * Overrides the Block.colorMultiplier() and Block.getRenderColor()
	 */
	public Parameter<Integer> colorMultiplier = new Parameter<>(null);

	/**
	 * Defines the color factor for the face (Block Level). Used for shading the faces depending on their orientation : <br>
	 * - NORTH/SOUTH : 0.8<br>
	 * - EAST/WEST : 0.6<br>
	 * - TOP : 1<br>
	 * - BOTTOM : 0.5<br>
	 */
	public Parameter<Float> colorFactor = new Parameter<>(1.0F);

	/** Defines brightness of the face (only used if useBlockBrightness = false) (Block Level) */
	public Parameter<Integer> brightness = new Parameter<>(15728640); // 983055 - 15728640

	/** Defines alpha transparency for the face (GL11.GL_BLEND needs to be set before). usePerVertexAlpha must be false (Block Level) */
	public Parameter<Integer> alpha = new Parameter<>(255);

	/** Defines the general direction of a face. Used for normals, and offset for AO and brightness calculation (Face Level) */
	public Parameter<EnumFacing> direction = new Parameter<>(null);

	/** Defines which direction will be used to get the block icon. If ForgeDirection.UNKNOWN, no texture will be used (Face Level) */
	public Parameter<EnumFacing> textureSide = new Parameter<>(null);

	/** Defines which block to take into account for AO calculation (Face Level) */
	public Parameter<int[][][]> aoMatrix = new Parameter<>(null);

	/** Defines whether to flip the texture on the U coordinates (Face Level) */
	public Parameter<Boolean> flipU = new Parameter<>(false);

	/** Defines whether to flip the texture on the U coordinates (Face Level) */
	public Parameter<Boolean> flipV = new Parameter<>(false);

	/** Defines whether a face should deducts its parameters before rendering. (Face level). */
	public Parameter<Boolean> deductParameters = new Parameter<>(false);

	/**
	 * Instantiates a new {@link RenderParameters}.
	 */
	public RenderParameters()
	{
		buildList();
	}

	/**
	 * Instantiates a new {@link RenderParameters} with the {@link Parameter} values inside <code>params</code>.
	 *
	 * @param params the params
	 */
	public RenderParameters(RenderParameters params)
	{
		this();
		merge(params);
	}

	/**
	 * Builds the list of {@link Parameter} in this {@link RenderParameters}.
	 */
	protected void buildList()
	{
		listParams = new LinkedList<>();
		listParams.add(renderAllFaces);
		listParams.add(useBlockBounds);
		listParams.add(renderBounds);
		listParams.add(useCustomTexture);
		listParams.add(applyTexture);
		listParams.add(icon);
		listParams.add(iconProvider);
		listParams.add(useWorldSensitiveIcon);
		listParams.add(useTexture);
		listParams.add(interpolateUV);
		listParams.add(rotateIcon);
		listParams.add(calculateAOColor);
		listParams.add(calculateBrightness);
		listParams.add(usePerVertexColor);
		listParams.add(usePerVertexAlpha);
		listParams.add(usePerVertexBrightness);
		listParams.add(useEnvironmentBrightness);
		listParams.add(useNormals);
		listParams.add(colorMultiplier);
		listParams.add(colorFactor);
		listParams.add(brightness);
		listParams.add(alpha);
		listParams.add(direction);
		listParams.add(textureSide);
		listParams.add(aoMatrix);
		listParams.add(flipU);
		listParams.add(flipV);
		listParams.add(deductParameters);
	}

	/**
	 * Gets the {@link Parameter} at <code>index</code>.
	 *
	 * @param index the index
	 * @return the parameter
	 */
	private Parameter<?> getParameter(int index)
	{
		if (index < 0 || index >= listParams.size())
			return null;
		return listParams.get(index);
	}

	/**
	 * Resets all {@link Parameter parameters} to their default value.
	 */
	public void reset()
	{
		for (Parameter<?> param : listParams)
			param.reset();
	}

	/**
	 * Merges all the values of <code>params</code> inside this {@link RenderParameters} {@link Parameter parameters}.
	 *
	 * @param params the params
	 */
	public void merge(RenderParameters params)
	{
		if (params == null)
			return;

		for (int i = 0; i < listParams.size(); i++)
			getParameter(i).merge(params.getParameter(i));
	}

	@Override
	public void setAlpha(int alpha)
	{
		this.alpha.set(alpha);
	}

	@Override
	public void setColor(int color)
	{
		this.colorMultiplier.set(color);
	}

	@Override
	public void setBrightness(int brightness)
	{
		this.brightness.set(brightness);
	}

	/**
	 * Merges <code>rp1</code> and <code>rp2</code> into a new {@link RenderParameters}.
	 *
	 * @param rp1 the rp1
	 * @param rp2 the rp2
	 * @return the render parameters
	 */
	public static RenderParameters merge(RenderParameters rp1, RenderParameters rp2)
	{
		if (rp1 == null)
			return rp2 == null ? new RenderParameters() : rp2.clone();

		RenderParameters rp = rp1.clone();
		rp.merge(rp2);
		return rp;
	}

	@Override
	public RenderParameters clone()
	{
		try
		{
			RenderParameters rp = (RenderParameters) super.clone();
			rp.renderAllFaces = renderAllFaces.clone();
			rp.useBlockBounds = useBlockBounds.clone();
			rp.renderBounds = renderBounds.clone();
			rp.useCustomTexture = useCustomTexture.clone();
			rp.applyTexture = applyTexture.clone();
			rp.iconProvider = iconProvider.clone();
			rp.icon = icon.clone();
			rp.useWorldSensitiveIcon = useWorldSensitiveIcon.clone();
			rp.useTexture = useTexture.clone();
			rp.interpolateUV = interpolateUV.clone();
			rp.rotateIcon = rotateIcon.clone();
			rp.calculateAOColor = calculateAOColor.clone();
			rp.calculateBrightness = calculateBrightness.clone();
			rp.usePerVertexColor = usePerVertexColor.clone();
			rp.usePerVertexAlpha = usePerVertexAlpha.clone();
			rp.usePerVertexBrightness = usePerVertexBrightness.clone();
			rp.useEnvironmentBrightness = useEnvironmentBrightness.clone();
			rp.useNormals = useNormals.clone();
			rp.colorMultiplier = colorMultiplier.clone();
			rp.colorFactor = colorFactor.clone();
			rp.brightness = brightness.clone();
			rp.alpha = alpha.clone();
			rp.direction = direction.clone();
			rp.textureSide = textureSide.clone();
			rp.aoMatrix = aoMatrix.clone();
			rp.flipU = flipU.clone();
			rp.flipV = flipV.clone();
			rp.deductParameters = deductParameters.clone();
			rp.buildList();
			return rp;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
