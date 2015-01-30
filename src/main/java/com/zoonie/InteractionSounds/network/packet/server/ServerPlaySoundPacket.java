package com.zoonie.InteractionSounds.network.packet.server;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.handler.SoundHandler;

public class ServerPlaySoundPacket implements IMessage
{
	String soundName, category, identifier;
	int x, y, z;
	float volume;

	public ServerPlaySoundPacket()
	{
	}

	public ServerPlaySoundPacket(String soundName, String category, String identifier, int x, int y, int z, float volume)
	{
		this.soundName = soundName;
		this.category = category;
		this.identifier = identifier;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int soundLength = bytes.readInt();
		char[] fileCars = new char[soundLength];
		for(int i = 0; i < soundLength; i++)
		{
			fileCars[i] = bytes.readChar();
		}
		soundName = String.valueOf(fileCars);

		int identLength = bytes.readInt();
		char[] identCars = new char[identLength];
		for(int i = 0; i < identLength; i++)
		{
			identCars[i] = bytes.readChar();
		}
		identifier = String.valueOf(identCars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);

		x = bytes.readInt();
		y = bytes.readInt();
		z = bytes.readInt();
		volume = bytes.readFloat();
		SoundHandler.playSound(soundName, category, identifier, x, y, z, volume);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(soundName.length());
		for(char c : soundName.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(identifier.length());
		for(char c : identifier.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(category.length());
		for(char c : category.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);
		bytes.writeFloat(volume);
	}

	public static class Handler implements IMessageHandler<ServerPlaySoundPacket, IMessage>
	{
		@Override
		public IMessage onMessage(ServerPlaySoundPacket message, MessageContext ctx)
		{
			return null;
		}
	}
}
