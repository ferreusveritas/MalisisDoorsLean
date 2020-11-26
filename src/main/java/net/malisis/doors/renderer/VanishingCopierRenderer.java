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

import java.util.Random;

import javax.vecmath.Matrix4f;

import net.malisis.doors.MalisisDoors;
import net.malisis.doors.MalisisDoors.Items;
import net.malisis.doors.block.IComponent;
import net.malisis.doors.renderer.DefaultRenderer;
import net.malisis.doors.renderer.animation.AnimationRenderer;
import net.malisis.doors.renderer.element.Shape;
import net.malisis.doors.renderer.icon.Icon;
import net.malisis.doors.renderer.icon.provider.IIconProvider;
import net.malisis.doors.renderer.model.MalisisModel;
import net.malisis.doors.renderer.model.loader.TextureModelLoader;
import net.malisis.doors.util.TransformBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author Ordinastie
 *
 */
public class VanishingCopierRenderer extends DefaultRenderer.Item
{

	private AnimationRenderer ar = new AnimationRenderer();
	private Shape shape;

	private Matrix4f thirdPersonRightHand = new TransformBuilder().translate(-.05F, .25F, .1F).rotate(45, -75, 0).scale(0.55F).get();
	private Matrix4f thirdPersonLeftHand = new TransformBuilder().translate(-.05F, .25F, .1F).rotate(0, 105, 45).scale(0.55F).get();

	@Override
	public void initialize()
	{
		super.initialize();
		Icon icon = IComponent.getComponent(IIconProvider.class, MalisisDoors.Items.vanishingCopierItem).getIcon();
		MalisisModel model = new MalisisModel(new TextureModelLoader(icon));
		shape = model.getShape("shape");
	}

	@Override
	public Matrix4f getTransform(Item item, TransformType tranformType)
	{
		switch (tranformType)
		{
			case THIRD_PERSON_LEFT_HAND:
				return thirdPersonLeftHand;
			case THIRD_PERSON_RIGHT_HAND:
				return thirdPersonRightHand;
			default:
				return DefaultRenderer.item.getTransform(item, tranformType);
		}

	}

	@Override
	public void render()
	{
		super.render();

		ItemStack copiedStack = Items.vanishingCopierItem.getVanishingOptions(itemStack).getSlot().getItemStack();
		if (copiedStack == null)
			return;

		draw();

		byte count = 1;
		if (tranformType != TransformType.GUI)
		{
			if (copiedStack.getCount() > 48)
				count = 5;
			else if (copiedStack.getCount() > 32)
				count = 4;
			else if (copiedStack.getCount() > 16)
				count = 3;
			else if (copiedStack.getCount() > 1)
				count = 2;
		}

		GlStateManager.translate(.2F, 0.70F, 0.5F);
		GlStateManager.scale(0.35F, 0.35F, 0.35F);
		if (tranformType != TransformType.GUI)
			GlStateManager.rotate(360 * ar.getElapsedTime() / 3000, 1, 1, 1);

		Random rand = new Random();
		rand.setSeed(187L);

		for (int j = 0; j < count; ++j)
		{
			if (count > 0)
			{
				float rx = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
				float ry = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
				float rz = (rand.nextFloat() * 2.0F - 1.0F) * 0.15F;
				GlStateManager.translate(rx, ry, rz);
			}

			Minecraft.getMinecraft().getRenderItem().renderItem(copiedStack, ItemCameraTransforms.TransformType.GROUND);
		}
	}

	@Override
	protected Shape getModelShape()
	{
		return shape;
	}
}
