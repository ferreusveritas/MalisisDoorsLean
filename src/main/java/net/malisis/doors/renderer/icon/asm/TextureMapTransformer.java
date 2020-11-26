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

package net.malisis.doors.renderer.icon.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.VarInsnNode;

import net.malisis.doors.asm.AsmHook;
import net.malisis.doors.asm.MalisisClassTransformer;
import net.malisis.doors.asm.mappings.McpMethodMapping;

/**
 * @author Ordinastie
 *
 */
public class TextureMapTransformer extends MalisisClassTransformer
{
	@Override
	public void registerHooks()
	{
		register(loadTextureAtlas());
	}

	private AsmHook loadTextureAtlas()
	{
		McpMethodMapping loadTextureAtlas = new McpMethodMapping(	"loadTextureAtlas",
																	"func_110571_b",
																	"net/minecraft/client/renderer/texture/TextureMap",
																	"(Lnet/minecraft/client/resources/IResourceManager;)V");
		AsmHook ah = new AsmHook(loadTextureAtlas);

		McpMethodMapping width = new McpMethodMapping(	"getCurrentWidth",
														"func_110935_a",
														"net/minecraft/client/renderer/texture/Stitcher",
														"()I");
		McpMethodMapping height = new McpMethodMapping(	"getCurrentHeight",
														"func_110936_b",
														"net/minecraft/client/renderer/texture/Stitcher",
														"()I");

		//		Stitcher stitcher = new Stitcher(0, 0, false, 0, 0);
		//		MalisisIcon.BLOCK_TEXTURE_WIDTH = stitcher.getCurrentWidth();
		//		MalisisIcon.BLOCK_TEXTURE_HEIGHT = stitcher.getCurrentHeight();
		//		 	ALOAD 3
		//		    INVOKEVIRTUAL net/minecraft/client/renderer/texture/Stitcher.getCurrentWidth ()I
		//		    PUTSTATIC net/malisis/doors/renderer/icon/MalisisIcon.BLOCK_TEXTURE_WIDTH : I
		//		    ALOAD 3
		//		    INVOKEVIRTUAL net/minecraft/client/renderer/texture/Stitcher.getCurrentHeight ()I
		//		    PUTSTATIC net/malisis/doors/renderer/icon/MalisisIcon.BLOCK_TEXTURE_HEIGHT : I

		InsnList insert = new InsnList();
		insert.add(new VarInsnNode(ALOAD, 3));
		insert.add(width.getInsnNode(INVOKEVIRTUAL));
		insert.add(new FieldInsnNode(PUTSTATIC, "net/malisis/doors/renderer/icon/Icon", "BLOCK_TEXTURE_WIDTH", "I"));
		insert.add(new VarInsnNode(ALOAD, 3));
		insert.add(height.getInsnNode(INVOKEVIRTUAL));
		insert.add(new FieldInsnNode(PUTSTATIC, "net/malisis/doors/renderer/icon/Icon", "BLOCK_TEXTURE_HEIGHT", "I"));

		ah.jumpToEnd().jump(-2).insert(insert);

		return ah;
	}
}
