package com.zoonie.InteractionSounds.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.handler.DelayedPlayHandler;

public class SoundNotFoundPacket implements IMessage
{
	String soundName, category;

	public SoundNotFoundPacket()
	{
	}

	public SoundNotFoundPacket(String soundName, String category)
	{
		this.soundName = soundName;
		this.category = category;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int fileLength = bytes.readInt();
		char[] fileChars = new char[fileLength];
		for(int i = 0; i < fileLength; i++)
		{
			fileChars[i] = bytes.readChar();
		}
		soundName = String.valueOf(fileChars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);

		DelayedPlayHandler.removeSound(soundName, category);
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
	}

	public static class Handler implements IMessageHandler<SoundNotFoundPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SoundNotFoundPacket message, MessageContext ctx)
		{
			return null;
		}
	}
}
