package com.zoonie.custominteractionsounds.network;

import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.network.message.SoundChunkMessage;
import com.zoonie.custominteractionsounds.network.message.SoundUploadedMessage;
import com.zoonie.custominteractionsounds.sound.Sound;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundHelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class NetworkHelper
{
	public static final int PARTITION_SIZE = 30000;

	public static void sendMessageToPlayer(IMessage message, EntityPlayerMP player)
	{
		ChannelHandler.network.sendTo(message, player);
	}

	@SideOnly(Side.CLIENT)
	public static void clientSoundUpload(Sound sound)
	{
		if(SoundHelper.getSoundLength(sound.getSoundLocation()) <= ServerSettingsConfig.MaxSoundLength)
		{
			sound.setState(Sound.SoundState.UPLOADING);
			uploadSound(sound, sound.getCategory());
		}
	}

	public static void serverSoundUpload(Sound sound, EntityPlayerMP player)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		if(soundBytes != null)
		{
			for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
			{
				byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
				ChannelHandler.network.sendTo(new SoundChunkMessage(sound.getSoundName(), sound.getCategory(), bytes), player);
			}
			ChannelHandler.network.sendTo(new SoundUploadedMessage(sound.getSoundName(), sound.getCategory()), player);
		}
	}

	private static void uploadSound(Sound sound, String category)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		if(soundBytes != null)
		{
			for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
			{
				byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
				ChannelHandler.network.sendToServer(new SoundChunkMessage(sound.getSoundName(), sound.getCategory(), bytes));
			}
			ChannelHandler.network.sendToServer(new SoundUploadedMessage(sound.getSoundName(), category));
		}
	}

	public static byte[] convertFileToByteArr(File file)
	{
		if(file != null && file.exists())
		{
			try
			{
				return FileUtils.readFileToByteArray(file);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File createFile(String fileName, String category)
	{
		byte[] byteFile = NetworkHandler.soundUploaded(fileName, category);
		if(byteFile != null && byteFile.length > 0 && !category.isEmpty() && !fileName.isEmpty())
		{
			File file = new File(SoundHandler.getSoundsFolder().getAbsolutePath() + File.separator + CustomInteractionSounds.MOD_NAME + File.separator + category + File.separator + fileName);
			try
			{
				FileUtils.writeByteArrayToFile(file, byteFile);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
}
