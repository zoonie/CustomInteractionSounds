package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.sound.SoundHandler;

public class PlaySoundMessage implements IMessage
{
	String soundName, category, caller;
	int dimensionId;
	int x, y, z;
	float volume;

	public PlaySoundMessage()
	{
	}

	public PlaySoundMessage(String name, String category, int dimensionId, int x, int y, int z, float volume, String caller)
	{
		this.soundName = name;
		this.category = category;
		this.dimensionId = dimensionId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
		this.caller = caller;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int nameLength = bytes.readInt();
		char[] nameChars = new char[nameLength];
		for(int i = 0; i < nameLength; i++)
		{
			nameChars[i] = bytes.readChar();
		}
		soundName = String.valueOf(nameChars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);

		dimensionId = bytes.readInt();
		x = bytes.readInt();
		y = bytes.readInt();
		z = bytes.readInt();
		volume = bytes.readFloat();

		int callerLength = bytes.readInt();
		char[] callerChars = new char[callerLength];
		for(int i = 0; i < callerLength; i++)
		{
			callerChars[i] = bytes.readChar();
		}
		caller = String.valueOf(callerChars);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(soundName.length());
		for(char c : soundName.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeInt(category.length());
		for(char c : category.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeInt(dimensionId);
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);
		bytes.writeFloat(volume);

		bytes.writeInt(caller.length());
		for(char c : caller.toCharArray())
		{
			bytes.writeChar(c);
		}
	}

	public static class ClientSideHandler implements IMessageHandler<PlaySoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(PlaySoundMessage message, MessageContext ctx)
		{
			if(!Minecraft.getMinecraft().thePlayer.getDisplayNameString().equals(message.caller))
				SoundHandler.playSound(message.soundName, message.category, message.x, message.y, message.z, message.volume);
			return null;
		}
	}

	public static class ServerSideHandler implements IMessageHandler<PlaySoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(PlaySoundMessage message, MessageContext ctx)
		{
			TargetPoint tp = new TargetPoint(message.dimensionId, message.x, message.y, message.z, 16);
			ChannelHandler.network.sendToAllAround(new PlaySoundMessage(message.soundName, message.category, message.dimensionId, message.x, message.y, message.z, message.volume, message.caller), tp);
			return null;
		}
	}
}
