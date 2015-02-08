package com.zoonie.InteractionSounds.network.packet;

import io.netty.buffer.ByteBuf;

import java.io.File;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.configuration.Config;
import com.zoonie.InteractionSounds.handler.DelayedPlayHandler;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.helper.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class SoundUploadedPacket implements IMessage
{
	private String category;
	private String soundName;
	private static File soundFile;

	public SoundUploadedPacket()
	{
	}

	public SoundUploadedPacket(String soundName, String category)
	{
		this.category = category;
		this.soundName = soundName;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int catLength = bytes.readInt();
		char[] catCars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catCars[i] = bytes.readChar();
		}
		category = String.valueOf(catCars);

		int fileLength = bytes.readInt();
		char[] fileCars = new char[fileLength];
		for(int i = 0; i < fileLength; i++)
		{
			fileCars[i] = bytes.readChar();
		}
		soundName = String.valueOf(fileCars);

		soundFile = NetworkHelper.createFile(soundName, category);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(category.length());
		for(char c : category.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(soundName.length());
		for(char c : soundName.toCharArray())
		{
			bytes.writeChar(c);
		}
	}

	public static class ServerSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx)
		{
			if(soundFile.length() <= Config.MaxSoundSize && SoundHelper.getSoundLength(soundFile) <= Config.MaxSoundLength)
			{
				SoundHandler.addSound(new SoundInfo(message.soundName, message.category), soundFile);
			}
			else
			{
				soundFile.delete();
				File folder = soundFile.getParentFile();
				if(folder.isDirectory() && folder.list() != null)
				{
					if(folder.list().length == 0)
					{
						folder.delete();
					}
				}
			}
			return null;
		}
	}

	public static class ClientSideHandler implements IMessageHandler<SoundUploadedPacket, IMessage>
	{
		@Override
		public IMessage onMessage(SoundUploadedPacket message, MessageContext ctx)
		{
			SoundHandler.addSound(new SoundInfo(message.soundName, message.category), soundFile);
			DelayedPlayHandler.onSoundReceived(message.soundName, message.category);

			return null;
		}
	}
}
