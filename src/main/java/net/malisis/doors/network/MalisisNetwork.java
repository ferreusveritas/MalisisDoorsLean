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

package net.malisis.doors.network;

import net.malisis.doors.IMalisisMod;
import net.malisis.doors.MalisisDoors;
import net.malisis.doors.inventory.message.OpenInventoryMessage;
import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.util.EntityUtils;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * {@link MalisisNetwork} is a wrapper around {@link SimpleNetworkWrapper} in order to ease the handling of discriminators.<br>
 * Each mod should instantiate a {@code MalisisNetwork} instance when constructed<br>
 * Ideally, {@link IMessageHandler} should be annotated with {@link AutoLoad} and register their packets inside their own parameterless
 * constructors.<br>
 * <br>
 * Example : {@link OpenInventoryMessage}.
 *
 *
 * @author Ordinastie
 */
public class MalisisNetwork extends SimpleNetworkWrapper
{
	/** The global discriminator for each packet. */
	private int discriminator = 0;
	/** Name of the channel used **/
	protected String name;

	/**
	 * Instantiates a new {@link MalisisNetwork}.
	 *
	 * @param channelName the channel name
	 */
	public MalisisNetwork(String channelName)
	{
		super(channelName);
		name = channelName;
	}

	/**
	 * Instantiates a new {@link MalisisNetwork}
	 *
	 * @param mod the mod
	 */
	public MalisisNetwork(IMalisisMod mod)
	{
		this(mod.getModId());
	}

	/**
	 * Send the {@link IMessage} to all the players currently watching that specific chunk.<br>
	 * The {@link IMessageHandler} for the message type should be on the CLIENT side.
	 *
	 * @param message the message
	 * @param chunk the chunk
	 */
	public void sendToPlayersWatchingChunk(IMessage message, Chunk chunk)
	{
		EntityUtils.getPlayersWatchingChunk(chunk).forEach(p -> sendTo(message, p));
	}

	/**
	 * Register a message with the next discriminator available.
	 *
	 * @param <REQ> the generic type
	 * @param <REPLY> the generic type
	 * @param messageHandler the message handler
	 * @param requestMessageType the request message type
	 * @param side the side
	 */
	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		super.registerMessage(messageHandler, requestMessageType, discriminator++, side);
		MalisisDoors.log.info("Registering " + messageHandler.getSimpleName() + " for " + requestMessageType.getSimpleName()
				+ " with discriminator " + discriminator + " in channel " + name);
	}

	/**
	 * Register a message with the next discriminator available.
	 *
	 * @param <REQ> the generic type
	 * @param <REPLY> the generic type
	 * @param messageHandler the message handler
	 * @param requestMessageType the request message type
	 * @param side the side
	 */
	public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(IMessageHandler<? super REQ, ? extends REPLY> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		super.registerMessage(messageHandler, requestMessageType, discriminator++, side);
		MalisisDoors.log.info("Registering " + messageHandler.getClass().getSimpleName() + " for " + requestMessageType.getSimpleName()
				+ " with discriminator " + discriminator + " in channel " + name);
	}

	/**
	 * Gets the next discriminator available.
	 *
	 * @return the next discriminator
	 */
	public int getNextDiscriminator()
	{
		return discriminator++;
	}
}
