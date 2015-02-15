package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.configuration.Config;
import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.interaction.Interaction;
import com.zoonie.InteractionSounds.interaction.KeyBindings;
import com.zoonie.InteractionSounds.sound.Sound;

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
		Config.UseServerMappings = buf.readBoolean();
		if(Config.UseServerMappings)
		{
			try
			{
				byte[] mappingsBytes = new byte[buf.readableBytes()];
				buf.readBytes(mappingsBytes);
				ByteArrayInputStream baos = new ByteArrayInputStream(mappingsBytes);
				ObjectInputStream ois = new ObjectInputStream(baos);
				MappingsConfigManager.mappings = (HashMap<Interaction, Sound>) ois.readObject();

				KeyBindings.deInit();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(Config.MaxSoundLength);
		buf.writeBoolean(Config.UseServerMappings);
		if(Config.UseServerMappings)
		{
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(baos);
				out.writeObject(MappingsConfigManager.mappings);
				buf.writeBytes(baos.toByteArray());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
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
