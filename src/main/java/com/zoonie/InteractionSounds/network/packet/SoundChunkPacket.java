package com.zoonie.InteractionSounds.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.handler.NetworkHandler;

public class SoundChunkPacket implements IMessage
{
	String soundName, category;
	byte[] soundChunk;

	public SoundChunkPacket()
	{
	}

	public SoundChunkPacket(String soundName, String category, byte[] soundChunk)
	{
		this.soundName = soundName;
		this.category = category;
		this.soundChunk = soundChunk;
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

		int soundByteLength = bytes.readInt();
		byte[] soundByteArr = new byte[soundByteLength];
		for(int i = 0; i < soundByteLength; i++)
		{
			soundByteArr[i] = bytes.readByte();
		}
		NetworkHandler.addSoundChunk(soundName, category, soundByteArr);
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
		bytes.writeInt(soundChunk.length);
		bytes.writeBytes(soundChunk);
	}

	public static class Handler implements IMessageHandler<SoundChunkPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SoundChunkPacket message, MessageContext ctx)
		{
			return null;
		}
	}
}
