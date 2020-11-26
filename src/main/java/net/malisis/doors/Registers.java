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

package net.malisis.doors;

import static net.malisis.doors.MalisisDoors.Blocks.bigDoorAcacia;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorBirch;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorDarkOak;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorIron;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorJungle;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorOak;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorRusty;
import static net.malisis.doors.MalisisDoors.Blocks.bigDoorSpruce;
import static net.malisis.doors.MalisisDoors.Blocks.blockMixer;
import static net.malisis.doors.MalisisDoors.Blocks.camoFenceGate;
import static net.malisis.doors.MalisisDoors.Blocks.customDoor;
import static net.malisis.doors.MalisisDoors.Blocks.doorFactory;
import static net.malisis.doors.MalisisDoors.Blocks.factoryDoor;
import static net.malisis.doors.MalisisDoors.Blocks.ironSlidingDoor;
import static net.malisis.doors.MalisisDoors.Blocks.jailDoor;
import static net.malisis.doors.MalisisDoors.Blocks.laboratoryDoor;
import static net.malisis.doors.MalisisDoors.Blocks.mixedBlock;
import static net.malisis.doors.MalisisDoors.Blocks.playerSensor;
import static net.malisis.doors.MalisisDoors.Blocks.shojiDoor;
import static net.malisis.doors.MalisisDoors.Blocks.slidingTrapDoor;
import static net.malisis.doors.MalisisDoors.Blocks.swapper;
import static net.malisis.doors.MalisisDoors.Blocks.vanishingBlock;
import static net.malisis.doors.MalisisDoors.Blocks.woodSlidingDoor;
import static net.malisis.doors.MalisisDoors.Items.customDoorItem;
import static net.malisis.doors.MalisisDoors.Items.factoryDoorItem;
import static net.malisis.doors.MalisisDoors.Items.ironSlidingDoorItem;
import static net.malisis.doors.MalisisDoors.Items.jailDoorItem;
import static net.malisis.doors.MalisisDoors.Items.laboratoryDoorItem;
import static net.malisis.doors.MalisisDoors.Items.shojiDoorItem;
import static net.malisis.doors.MalisisDoors.Items.slidingTrapDoorItem;
import static net.malisis.doors.MalisisDoors.Items.vanishingCopierItem;
import static net.malisis.doors.MalisisDoors.Items.woodSlidingDoorItem;

import net.malisis.doors.MalisisDoors.Sounds;
import net.malisis.doors.bigdoors.Door3x3;
import net.malisis.doors.bigdoors.Door3x3Tile;
import net.malisis.doors.block.BlockMixer;
import net.malisis.doors.block.CustomDoor;
import net.malisis.doors.block.Door;
import net.malisis.doors.block.DoorFactory;
import net.malisis.doors.block.FenceGate;
import net.malisis.doors.block.MixedBlock;
import net.malisis.doors.block.PlayerSensor;
import net.malisis.doors.block.Swapper;
import net.malisis.doors.block.TrapDoor;
import net.malisis.doors.block.VanishingBlock;
import net.malisis.doors.descriptor.Curtain;
import net.malisis.doors.descriptor.FactoryDoor;
import net.malisis.doors.descriptor.GlassDoor;
import net.malisis.doors.descriptor.JailDoor;
import net.malisis.doors.descriptor.LaboratoryDoor;
import net.malisis.doors.descriptor.ShojiDoor;
import net.malisis.doors.descriptor.SlidingTrapDoor;
import net.malisis.doors.descriptor.WoodTrapDoor;
import net.malisis.doors.item.CustomDoorItem;
import net.malisis.doors.item.DoorItem;
import net.malisis.doors.item.VanishingCopierItem;
import net.malisis.doors.registry.MalisisRegistry;
import net.malisis.doors.tileentity.BlockMixerTileEntity;
import net.malisis.doors.tileentity.CustomDoorTileEntity;
import net.malisis.doors.tileentity.DoorFactoryTileEntity;
import net.malisis.doors.tileentity.DoorTileEntity;
import net.malisis.doors.tileentity.FenceGateTileEntity;
import net.malisis.doors.tileentity.MixedBlockTileEntity;
import net.malisis.doors.tileentity.SwapperTileEntity;
import net.malisis.doors.tileentity.TrapDoorTileEntity;
import net.malisis.doors.tileentity.VanishingDiamondTileEntity;
import net.malisis.doors.tileentity.VanishingTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.item.EnumDyeColor;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Registers {
	
	public static void init() {
		//Registration order affect items in the creative tab
		
		registerDoors();
		
		registerCustomDoor();
		
		registerTrapDoors();
		
		registerCamoFenceGate();
		
		registerBigDoors();
		
		registerDoorFactory();
		
		GameRegistry.registerTileEntity(DoorTileEntity.class, "doorTileEntity");
		GameRegistry.registerTileEntity(TrapDoorTileEntity.class, "trapDoorTileEntity");
		GameRegistry.registerTileEntity(FenceGateTileEntity.class, "fenceGateTileEntity");
		
		registerVanishingBlock();
		
		registerMixedBlock();
		
		registerPlayerSensor();
		
		registerSwapper();
		
		registerSounds();
	}
	
	private static void registerDoors() {
		DoorDescriptor desc;
		
		//Glass Doors
		desc = new GlassDoor(Material.WOOD).register();
		woodSlidingDoor = (Door) desc.getBlock();
		woodSlidingDoorItem = (DoorItem) desc.getItem();
		
		desc = new GlassDoor(Material.IRON).register();
		ironSlidingDoor = (Door) desc.getBlock();
		ironSlidingDoorItem = (DoorItem) desc.getItem();
		
		//Jail Door
		desc = new JailDoor().register();
		jailDoor = (Door) desc.getBlock();
		jailDoorItem = (DoorItem) desc.getItem();
		
		//Laboratory Door
		desc = new LaboratoryDoor().register();
		laboratoryDoor = (Door) desc.getBlock();
		laboratoryDoorItem = (DoorItem) desc.getItem();
		
		//Factory Door
		desc = new FactoryDoor().register();
		factoryDoor = (Door) desc.getBlock();
		factoryDoorItem = (DoorItem) desc.getItem();
		
		//Shoji Door
		desc = new ShojiDoor().register();
		shojiDoor = (Door) desc.getBlock();
		shojiDoorItem = (DoorItem) desc.getItem();
		
		//Curtains
		for (EnumDyeColor color : EnumDyeColor.values())
			new Curtain(color).register();
	}
	
	private static void registerTrapDoors() {
		DoorDescriptor desc = new SlidingTrapDoor().register();
		slidingTrapDoor = (TrapDoor) desc.getBlock();
		slidingTrapDoorItem = desc.getItem();
		
		for (WoodTrapDoor.Type type : WoodTrapDoor.Type.values())
		{
			desc = new WoodTrapDoor(type).register();
		}
	}
	
	private static void registerCamoFenceGate() {
		camoFenceGate = new FenceGate(FenceGate.Type.CAMO);
		camoFenceGate.register();
	}
	
	private static void registerDoorFactory() {
		doorFactory = new DoorFactory();
		doorFactory.register();
		
		GameRegistry.registerTileEntity(DoorFactoryTileEntity.class, "doorFactoryTileEntity");
	}
	
	private static void registerCustomDoor() {
		customDoor = new CustomDoor();
		customDoor.register();
		
		customDoorItem = new CustomDoorItem();
		customDoorItem.register();
		
		GameRegistry.registerTileEntity(CustomDoorTileEntity.class, "customDoorTileEntity");
	}
	
	private static void registerBigDoors() {
		
		//3x3 doors
		bigDoorOak = new Door3x3(Door3x3.Type.OAK);
		bigDoorOak.register();
		
		bigDoorSpruce = new Door3x3(Door3x3.Type.SPRUCE);
		bigDoorSpruce.register();
		
		bigDoorBirch = new Door3x3(Door3x3.Type.BIRCH);
		bigDoorBirch.register();
		
		bigDoorJungle = new Door3x3(Door3x3.Type.JUNGLE);
		bigDoorJungle.register();
		
		bigDoorAcacia = new Door3x3(Door3x3.Type.ACACIA);
		bigDoorAcacia.register();
		
		bigDoorDarkOak = new Door3x3(Door3x3.Type.DARK_OAK);
		bigDoorDarkOak.register();
		
		bigDoorIron = new Door3x3(Door3x3.Type.IRON);
		bigDoorIron.register();
		
		bigDoorRusty = new Door3x3(Door3x3.Type.RUSTY);
		bigDoorRusty.register();
		
		GameRegistry.registerTileEntity(Door3x3Tile.class, "door3x3");
	}
	
	
	private static void registerVanishingBlock() {
		vanishingBlock = new VanishingBlock();
		vanishingBlock.register();
		
		GameRegistry.registerTileEntity(VanishingTileEntity.class, "vanishingTileEntity");
		GameRegistry.registerTileEntity(VanishingDiamondTileEntity.class, "vanishingDiamondTileEntity");
		
		vanishingCopierItem = new VanishingCopierItem();
		vanishingCopierItem.register();
	}
	
	private static void registerMixedBlock() {
		blockMixer = new BlockMixer();
		blockMixer.register();
		mixedBlock = new MixedBlock();
		mixedBlock.register();
		
		GameRegistry.registerTileEntity(BlockMixerTileEntity.class, "blockMixerTileEntity");
		GameRegistry.registerTileEntity(MixedBlockTileEntity.class, "mixedBlockTileEntity");
	}
	
	private static void registerPlayerSensor() {
		playerSensor = new PlayerSensor();
		playerSensor.register();
	}
	
	private static void registerSwapper() {
		swapper = new Swapper();
		swapper.register();
		
		GameRegistry.registerTileEntity(SwapperTileEntity.class, "swapperTileEntity");
	}
	
	private static void registerSounds() {
		Sounds.portal = MalisisRegistry.registerSound(MalisisDoors.modid, "portal");
	}
	
}
