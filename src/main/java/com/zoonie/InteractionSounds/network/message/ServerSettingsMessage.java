package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.configuration.Config;

public class ServerSettingsMessage implements IMessage
{
	private double maxSoundLength;
	private long maxSoundSize;

	public ServerSettingsMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		maxSoundLength = buf.readDouble();
		maxSoundSize = buf.readLong();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(Config.MaxSoundLength);
		buf.writeLong(Config.MaxSoundSize);
	}

	public static class Handler implements IMessageHandler<ServerSettingsMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ServerSettingsMessage message, MessageContext ctx)
		{
			Config.MaxSoundLength = message.maxSoundLength;
			Config.MaxSoundSize = message.maxSoundSize;
			return null;
		}
	}
}
