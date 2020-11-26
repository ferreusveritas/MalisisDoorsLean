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

package net.malisis.doors.renderer.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.malisis.doors.renderer.RenderParameters;
import net.malisis.doors.renderer.animation.transformation.ITransformable;
import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.util.Vector;
import net.minecraft.util.EnumFacing;

public class Face implements ITransformable.Translate, ITransformable.Rotate
{
	/** Name of this {@link Face}. */
	protected String name;
	/** List of {@link Vertex Vertexes} of this {@link Face}. */
	protected Vertex[] vertexes;
	/** {@link RenderParameters} for this {@link Face}. */
	protected RenderParameters params;

	/**
	 * Instantiates a new {@link Face}.
	 *
	 * @param vertexes the vertexes
	 * @param params the params
	 */
	public Face(Vertex[] vertexes, RenderParameters params)
	{
		this.vertexes = vertexes;
		this.params = params != null ? params : new RenderParameters();
		this.setName(null);
	}

	/**
	 * Instantiates a new {@link Face}.
	 *
	 * @param vertexes the vertexes
	 */
	public Face(Vertex... vertexes)
	{
		this(vertexes, null);
	}

	/**
	 * Instantiates a new {@link Face}.
	 *
	 * @param vertexes the vertexes
	 */
	public Face(List<Vertex> vertexes)
	{
		this(vertexes.toArray(new Vertex[0]), null);
	}

	/**
	 * Instantiates a new {@link Face}.
	 *
	 * @param face the face
	 */
	public Face(Face face)
	{
		this(face, new RenderParameters(face.params));
	}

	/**
	 * Instantiates a new {@link Face}.
	 *
	 * @param face the face
	 * @param params the params
	 */
	public Face(Face face, RenderParameters params)
	{
		Vertex[] faceVertexes = face.getVertexes();
		this.vertexes = new Vertex[faceVertexes.length];
		for (int i = 0; i < faceVertexes.length; i++)
			vertexes[i] = new Vertex(faceVertexes[i]);
		this.params = params != null ? params : new RenderParameters();
		name = face.name;
	}

	/**
	 * Sets the base name for this {@link Face}. If the name specified is null, it is automatically determined based on the {@link Vertex}
	 * positions.
	 *
	 * @param name the base name
	 */
	public void setName(String name)
	{
		if (name == null)
		{
			name = "";
			HashMap<String, Integer> map = new HashMap<>();
			String[] dirs = new String[] { "North", "South", "East", "West", "Top", "Bottom" };
			for (String dir : dirs)
			{
				map.put(dir, 0);
				for (Vertex v : vertexes)
				{
					if (v.name().contains(dir))
						map.put(dir, map.get(dir) + 1);
				}
				if (map.get(dir) == 4)
					name = dir;
			}
		}

		this.name = name;
	}

	/**
	 * Gets the base name of this {@link Face}.
	 *
	 * @return the base name
	 */
	public String name()
	{
		return name;
	}

	/**
	 * Gets the {@link Vertex vertexes} of this {@link Face}.
	 *
	 * @return the vertexes
	 */
	public Vertex[] getVertexes()
	{
		return vertexes;
	}

	/**
	 * Gets a list of {@link Vertex} with a base name containing <b>name</b>.
	 *
	 * @param name the name
	 * @return the vertexes
	 */
	public List<Vertex> getVertexes(String name)
	{
		List<Vertex> vertexes = new ArrayList<>();
		for (Vertex v : getVertexes())
		{
			if (v.baseName().toLowerCase().contains(name.toLowerCase()))
				vertexes.add(v);
		}
		return vertexes;
	}

	/**
	 * Sets the {@link RenderParameters} for this {@link Face}.
	 *
	 * @param params the parameters. If {@code null}, sets default parameters
	 * @return this {@link Face}
	 */
	public Face setParameters(RenderParameters params)
	{
		this.params = params != null ? params : new RenderParameters();
		return this;
	}

	/**
	 * Gets the {@link RenderParameters} of this {@link Face}.
	 *
	 * @return the parameters
	 */
	public RenderParameters getParameters()
	{
		return params;
	}

	/**
	 * Sets the color for this {@link Face}.
	 *
	 * @param color the color
	 * @return the face
	 */
	public Face setColor(int color)
	{
		for (Vertex v : vertexes)
			v.setColor(color);
		return this;
	}

	/**
	 * Sets the alpha for this {@link Face}.
	 *
	 * @param alpha the alpha
	 * @return the face
	 */
	public Face setAlpha(int alpha)
	{
		for (Vertex v : vertexes)
			v.setAlpha(alpha);
		return this;
	}

	/**
	 * Sets the brightness for this {@link Face}.
	 *
	 * @param brightness the brightness
	 * @return the face
	 */
	public Face setBrightness(int brightness)
	{
		for (Vertex v : vertexes)
			v.setBrightness(brightness);
		return this;
	}

	//#region Textures manipaluation
	/**
	 * Sets standard UVs for this {@link Face}. Sets UVs from 0 to 1 if this face has 4 vertexes.
	 *
	 * @return the face
	 */
	public Face setStandardUV()
	{
		if (vertexes.length != 4)
			return this;
		vertexes[0].setUV(0, 0);
		vertexes[1].setUV(0, 1);
		vertexes[2].setUV(1, 1);
		vertexes[3].setUV(1, 0);
		return this;
	}

	/**
	 * Interpolate UVs according to the {@link Face} position in the block space.
	 *
	 * @return the face
	 */
	public Face interpolateUV()
	{
		return setTexture(null, false, false, true);
	}

	/**
	 * Sets the {@link Icon} to use for this {@link Face}.
	 *
	 * @return the face
	 */
	public Face setTexture(Icon icon)
	{
		return setTexture(icon, params.flipU.get(), params.flipV.get(), false);
	}

	/**
	 * Sets the {@link Icon} to use for this {@link Face}.
	 *
	 * @param icon the icon
	 * @param flippedU whether to mirror the texture horizontally
	 * @param flippedV whether to mirror the texture vertically
	 * @param interpolate whether to interpolate the UVs based on the face position in the block space.
	 * @return the face
	 */
	public Face setTexture(Icon icon, boolean flippedU, boolean flippedV, boolean interpolate)
	{
		int[] cos = { 1, 0, -1, 0 };
		int[] sin = { 0, 1, 0, -1 };

		float u = 0;
		float v = 0;
		float U = 1;
		float V = 1;
		int rotation = 0;
		if (icon != null)
		{
			u = icon.getMinU();
			v = icon.getMinV();
			U = icon.getMaxU();
			V = icon.getMaxV();
			rotation = icon.getRotation();
		}

		int a = rotation & 3;
		int s = sin[a];
		int c = cos[a];

		for (Vertex vertex : vertexes)
		{
			double factorU = interpolate ? getFactorU(vertex) : vertex.getU();
			double factorV = interpolate ? getFactorV(vertex) : vertex.getV();
			double newU = c * (factorU - .5F) - s * (factorV - .5F) + .5F;
			double newV = s * (factorU - .5F) + c * (factorV - .5F) + .5F;
			newU = interpolate(u, U, newU, flippedU);
			newV = interpolate(v, V, newV, flippedV);
			vertex.setUV(newU, newV);
		}

		return this;
	}

	/**
	 * Gets the factor of U based on the vertex position and the direction of this {@link Face}.
	 *
	 * @param vertex the vertex
	 * @return the factor u
	 */
	private double getFactorU(Vertex vertex)
	{
		if (params.direction.get() == null)
			return vertex.getU();

		switch (params.direction.get())
		{
			case EAST:
			case WEST:
				return vertex.getZ();
			case NORTH:
			case SOUTH:
			case UP:
			case DOWN:
				return vertex.getX();
			default:
				return 0;
		}
	}

	/**
	 * Gets the factor of V based on the vertex position and the direction of this {@link Face}.
	 *
	 * @param vertex the vertex
	 * @return the factor v
	 */
	private double getFactorV(Vertex vertex)
	{
		if (params.direction.get() == null)
			return vertex.getV();

		switch (params.direction.get())
		{
			case EAST:
			case WEST:
			case NORTH:
			case SOUTH:
				return 1 - vertex.getY();
			case UP:
			case DOWN:
				return vertex.getZ();
			default:
				return 0;
		}
	}

	/**
	 * Interpolates the factor value between min and max
	 *
	 * @param min the min
	 * @param max the max
	 * @param factor the factor
	 * @param flipped the flipped
	 * @return the float
	 */
	private float interpolate(float min, float max, double factor, boolean flipped)
	{
		if (factor > 1)
			factor = 1;
		if (factor < 0)
			factor = 0;
		if (flipped)
			factor = 1 - factor;

		return min + (max - min) * (float) factor;
	}

	//#end Textures manipulation

	//#region Transformations
	@Override
	public void translate(float x, float y, float z)
	{
		for (Vertex v : vertexes)
			v.translate(x, y, z);
	}

	public void scale(float f)
	{
		scale(f, f, f, 0, 0, 0);
	}

	public void scale(float f, float offset)
	{
		scale(f, f, f, offset, offset, offset);
	}

	public void scale(float fx, float fy, float fz)
	{
		scale(fx, fy, fz, 0, 0, 0);
	}

	public void scale(float fx, float fy, float fz, float offsetX, float offsetY, float offsetZ)
	{
		for (Vertex v : vertexes)
			v.scale(fx, fy, fz, offsetX, offsetY, offsetZ);
	}

	@Override
	public void rotate(float angle, float x, float y, float z, float offsetX, float offsetY, float offsetZ)
	{
		rotateAroundX(angle * x, offsetX, offsetY, offsetZ);
		rotateAroundY(angle * y, offsetX, offsetY, offsetZ);
		rotateAroundZ(angle * z, offsetX, offsetY, offsetZ);
	}

	public void rotateAroundX(double angle)
	{
		rotateAroundX(angle, 0.5, 0.5, 0.5);
	}

	public void rotateAroundX(double angle, double centerX, double centerY, double centerZ)
	{
		for (Vertex v : vertexes)
			v.rotateAroundX(angle, centerX, centerY, centerZ);
	}

	public void rotateAroundY(double angle)
	{
		rotateAroundY(angle, 0.5, 0.5, 0.5);
	}

	public void rotateAroundY(double angle, double centerX, double centerY, double centerZ)
	{
		for (Vertex v : vertexes)
			v.rotateAroundY(angle, centerX, centerY, centerZ);
	}

	public void rotateAroundZ(double angle)
	{
		rotateAroundZ(angle, 0.5, 0.5, 0.5);
	}

	public void rotateAroundZ(double angle, double centerX, double centerY, double centerZ)
	{
		for (Vertex v : vertexes)
			v.rotateAroundZ(angle, centerX, centerY, centerZ);
	}

	//#end Transformations

	/**
	 * Automatically calculate AoMatrix for this {@link Face}. Only works for regular N/S/E/W/T/B faces
	 *
	 * @param offset the offset
	 * @return the aoMatrix
	 */
	public int[][][] calculateAoMatrix(EnumFacing offset)
	{
		int[][][] aoMatrix = new int[vertexes.length][3][3];

		for (int i = 0; i < vertexes.length; i++)
			aoMatrix[i] = vertexes[i].getAoMatrix(offset);

		return aoMatrix;
	}

	/**
	 * Gets the vertexes normals for this {@link Face}.
	 *
	 * @return the vertexes normals
	 */
	public Vector[] getVertexNormals()
	{
		Vector[] normals = new Vector[vertexes.length];
		int i = 0;
		for (Vertex v : vertexes)
			normals[i++] = new Vector(v.getX(), v.getY(), v.getZ());
		return normals;
	}

	/**
	 * Calculates the normal of this {@link Face} based on the vertex coordinates.
	 */
	public void calculateNormal()
	{
		calculateNormal(getVertexNormals());
	}

	/**
	 * Calculates normal of this {@link Face} using the vertex normals provided.
	 *
	 * @param normals the normals
	 * @return the vector
	 */
	public Vector calculateNormal(Vector[] normals)
	{
		if (normals == null || normals.length != vertexes.length)
			normals = getVertexNormals();

		double x = 0;
		double y = 0;
		double z = 0;

		for (int i = 0; i < vertexes.length; i++)
		{
			Vertex current = vertexes[i];
			Vertex next = vertexes[(i + 1) % vertexes.length];

			x += (current.getY() - next.getY()) * (current.getZ() + next.getZ());
			y += (current.getZ() - next.getZ()) * (current.getX() + next.getX());
			z += (current.getX() - next.getX()) * (current.getY() + next.getY());
		}

		int factor = 1000;
		Vector normal = new Vector(	(float) Math.round(x * factor) / factor,
									(float) Math.round(y * factor) / factor,
									(float) Math.round(z * factor) / factor);
		normal.normalize();
		return normal;
	}

	/**
	 * Deducts the parameters for this {@link Face} based on the calculated normal.
	 */
	public void deductParameters()
	{
		deductParameters(getVertexNormals());
	}

	/**
	 * Deducts the {@link RenderParameters} for this {@link Face} based on the specified normals
	 *
	 * @param normals the vertex normals
	 */
	public void deductParameters(Vector[] normals)
	{
		Vector normal = calculateNormal(normals);
		EnumFacing dir = null;

		if (normal.x == 0 && normal.y == 0)
		{
			if (normal.z == 1)
				dir = EnumFacing.SOUTH;
			else if (normal.z == -1)
				dir = EnumFacing.NORTH;
		}
		else if (normal.x == 0 && normal.z == 0)
		{
			if (normal.y == 1)
				dir = EnumFacing.UP;
			else if (normal.y == -1)
				dir = EnumFacing.DOWN;
		}
		else if (normal.y == 0 && normal.z == 0)
		{
			if (normal.x == 1)
				dir = EnumFacing.EAST;
			else if (normal.x == -1)
				dir = EnumFacing.WEST;
		}

		params.direction.set(dir);
		params.textureSide.set(dir);
		if (dir != null)
			params.aoMatrix.set(calculateAoMatrix(dir));

		//fry's patent
		float f = (float) ((normal.x * normal.x * 0.6 + normal.y * (normal.y * 3 + 1) / 4 + normal.z * normal.z * 0.8));
		params.colorFactor.set(f);
	}

	@Override
	public String toString()
	{
		String s = name() + " {";
		for (Vertex v : vertexes)
			s += v.name() + ", ";
		return s + "}";
	}

	/**
	 * Gets a {@link Face} name from a {@link EnumFacing}.
	 *
	 * @param dir the dir
	 * @return the name
	 */
	public static String nameFromDirection(EnumFacing dir)
	{
		if (dir == EnumFacing.UP)
			return "top";
		else if (dir == EnumFacing.DOWN)
			return "bottom";
		else
			return dir.toString();
	}

}
