package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.configuration.Config;

public class ServerSettingsMessage implements IMessage
{
	private double maxSoundLength;

	public ServerSettingsMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		maxSoundLength = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(Config.MaxSoundLength);
	}

	public static class Handler implements IMessageHandler<ServerSettingsMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ServerSettingsMessage message, MessageContext ctx)
		{
			Config.MaxSoundLength = message.maxSoundLength;
			return null;
		}
	}
}
