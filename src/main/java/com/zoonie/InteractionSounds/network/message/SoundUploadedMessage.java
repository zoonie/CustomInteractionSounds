package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.configuration.ServerSettingsConfig;
import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.network.NetworkHandler;
import com.zoonie.InteractionSounds.network.NetworkHelper;
import com.zoonie.InteractionSounds.sound.DelayedPlayHandler;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class SoundUploadedMessage implements IMessage
{
	private String category;
	private String soundName;
	private static File soundFile;

	public SoundUploadedMessage()
	{
	}

	public SoundUploadedMessage(String soundName, String category)
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

	public static class ServerSideHandler implements IMessageHandler<SoundUploadedMessage, IMessage>
	{
		@Override
		public IMessage onMessage(SoundUploadedMessage message, MessageContext ctx)
		{
			if(SoundHelper.getSoundLength(soundFile) <= ServerSettingsConfig.MaxSoundLength)
			{
				SoundInfo soundInfo = new SoundInfo(message.soundName, message.category);

				for(EntityPlayerMP player : NetworkHandler.waiting.get(soundInfo))
				{
					NetworkHelper.serverSoundUpload(new Sound(soundFile), player);
				}

				SoundHandler.addSound(soundInfo, soundFile);
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
			for(EntityPlayerMP player : (List<EntityPlayerMP>) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
			{
				ChannelHandler.network.sendTo(new ServerSoundsMessage(player, new ArrayList<Sound>(SoundHandler.getSounds().values())), player);
			}
			return null;
		}
	}

	public static class ClientSideHandler implements IMessageHandler<SoundUploadedMessage, IMessage>
	{
		@Override
		public IMessage onMessage(SoundUploadedMessage message, MessageContext ctx)
		{
			SoundHandler.addSound(new SoundInfo(message.soundName, message.category), soundFile);
			DelayedPlayHandler.onSoundReceived(new SoundInfo(message.soundName, message.category));

			return null;
		}
	}
}
