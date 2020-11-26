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

package net.malisis.doors.util;

/**
 * This class allows condensed and silenced exception catching.
 *
 * @author Ordinastie
 */
public class Silenced
{
	public static <T> T get(Supplier<T> supplier)
	{
		try
		{
			return supplier.get();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static boolean exec(Exec exec)
	{
		try
		{
			exec.exec();
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static <T> boolean accept(Consumer<T> consumer, T t)
	{
		try
		{
			consumer.accept(t);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static <T, U> boolean accept(BiConsumer<T, U> biconsumer, T t, U u)
	{
		try
		{
			biconsumer.accept(t, u);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static <T, U> T apply(Function<T, U> function, U u)
	{
		try
		{
			return function.apply(u);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static interface Supplier<T>
	{
		public T get() throws Exception;
	}

	public static interface Exec
	{
		public void exec() throws Exception;
	}

	public static interface Consumer<T>
	{
		public void accept(T t) throws Exception;
	}

	public static interface BiConsumer<T, U>
	{
		public void accept(T t, U u) throws Exception;
	}

	public static interface Function<T, U>
	{
		public T apply(U u) throws Exception;
	}
}