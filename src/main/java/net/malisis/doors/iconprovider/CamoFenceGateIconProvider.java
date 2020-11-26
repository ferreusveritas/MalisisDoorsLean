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

package net.malisis.doors.iconprovider;

import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.renderer.icon.provider.IBlockIconProvider;
import net.malisis.doors.util.TileEntityUtils;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.tileentity.FenceGateTileEntity;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * @author Ordinastie
 *
 */
public class CamoFenceGateIconProvider implements IBlockIconProvider
{
	private Icon defaultIcon = Icon.from(MalisisDoors.modid + ":blocks/camo_fencegate");

	@Override
	public Icon getIcon()
	{
		return defaultIcon;
	}

	@Override
	public Icon getIcon(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side)
	{
		FenceGateTileEntity te = TileEntityUtils.getTileEntity(FenceGateTileEntity.class, world, pos);
		if (te == null)
			return getIcon();

		IBlockState camoState = te.getCamoState();
		if (camoState == null)
			return getIcon();

		return Icon.from(camoState);
	}

	@Override
	public Icon getIcon(IBlockState state, EnumFacing side)
	{
		//called for ITEM
		return defaultIcon;
	}

	public static CamoFenceGateIconProvider get()
	{
		return new CamoFenceGateIconProvider();
	}
}
