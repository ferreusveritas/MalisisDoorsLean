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

package net.malisis.doors.descriptor;

import net.malisis.doors.DoorDescriptor;
import net.malisis.doors.DoorRegistry;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.movement.SlidingUpDoorMovement;
import net.malisis.doors.sound.PneumaticSound;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * @author Ordinastie
 *
 */
public class LaboratoryDoor extends DoorDescriptor
{
	public LaboratoryDoor()
	{
		//Block
		setMaterial(Material.IRON);
		setHardness(4.0F);
		setSoundType(SoundType.METAL);
		setName("laboratory_door");
		setTextureName(MalisisDoors.modid, "laboratory_door");

		//te
		setOpeningTime(12);
		setDoubleDoor(false);
		setMovement(DoorRegistry.getMovement(SlidingUpDoorMovement.class));
		setSound(DoorRegistry.getSound(PneumaticSound.class));

		//Item
		setTab(MalisisDoors.tab);
	}
}
