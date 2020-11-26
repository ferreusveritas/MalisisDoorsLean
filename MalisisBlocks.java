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

package net.malisis.blocks;

import net.malisis.blocks.block.BlockMixer;
import net.malisis.blocks.block.MixedBlock;
import net.malisis.blocks.block.PlayerSensor;
import net.malisis.blocks.block.Swapper;
import net.malisis.blocks.block.VanishingBlock;
import net.malisis.blocks.block.VanishingDiamondBlock;
import net.malisis.blocks.item.VanishingCopierItem;
import net.malisis.doors.IMalisisMod;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.network.MalisisNetwork;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author Ordinastie
 *
 */
@Mod(modid = MalisisBlocks.modid, name = MalisisBlocks.modname, version = MalisisBlocks.version, dependencies = "required-after:malisisdoors", acceptedMinecraftVersions = "[1.12, 1.13)")
public class MalisisBlocks implements IMalisisMod
{

	public static final String modid = "malisisblocks";
	public static final String modname = "Malisis Blocks";
	public static final String version = "${version}";

	public static MalisisBlocks instance;
	public static MalisisNetwork network;

	public static CreativeTabs tab = new MalisisBlocksTab();

	public MalisisBlocks()
	{
		instance = this;
		network = new MalisisNetwork(this);
		MalisisDoors.registerMod(this);
	}

	@Override
	public String getModId()
	{
		return modid;
	}

	@Override
	public String getName()
	{
		return modname;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Registers.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{}

	public static class Blocks
	{
		public static BlockMixer blockMixer;
		public static MixedBlock mixedBlock;
		public static VanishingBlock vanishingBlock;
		public static VanishingDiamondBlock vanishingDiamondBlock;
		public static PlayerSensor playerSensor;
		public static Swapper swapper;
	}

	public static class Items
	{
		public static VanishingCopierItem vanishingCopierItem;
	}

	public static class Sounds
	{
		public static SoundEvent portal;
	}

}
