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

package net.malisis.doors.renderer.animation.transformation;

import java.util.ArrayList;

/**
 * @author Ordinastie
 *
 */
public class ParallelTransformation extends Transformation<ParallelTransformation, ITransformable>
{
	/** List of transformations. */
	protected ArrayList<Transformation<?, ?>> listTransformations = new ArrayList<>();

	/**
	 * Instantiates a new {@link ParallelTransformation}.
	 *
	 * @param transformations the transformations
	 */
	public ParallelTransformation(Transformation<?, ?>... transformations)
	{
		addTransformations(transformations);
	}

	/**
	 * Gets this {@link ParallelTransformation}
	 *
	 * @return the parallel transformation
	 */
	@Override
	public ParallelTransformation self()
	{
		return this;
	}

	/**
	 * Adds the {@link Transformation} this to {@link ParallelTransformation}
	 *
	 * @param transformations the transformations
	 * @return the parallel transformation
	 */
	public ParallelTransformation addTransformations(Transformation<?, ?>... transformations)
	{
		for (Transformation<?, ?> transformation : transformations)
		{
			duration = Math.max(duration, transformation.totalDuration());
			listTransformations.add(transformation);
		}

		return this;
	}

	/**
	 * Calculates the tranformation.
	 *
	 * @param transformable the transformable
	 * @param comp the comp
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void doTransform(ITransformable transformable, float comp)
	{
		if (listTransformations.size() == 0)
			return;

		for (Transformation transformation : listTransformations)
			transformation.transform(transformable, elapsedTimeCurrentLoop);
	}

	/**
	 * Sets this trasformation in reverse.
	 *
	 * @param reversed the reversed
	 * @return the parallel transformation
	 */
	@Override
	public ParallelTransformation reversed(boolean reversed)
	{
		if (!reversed)
			return this;

		for (Transformation<?, ?> transformation : listTransformations)
			transformation.reversed(true);

		return this;
	}
}
