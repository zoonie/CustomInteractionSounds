package com.zoonie.InteractionSounds.helper;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.network.packet.SoundChunkPacket;
import com.zoonie.InteractionSounds.network.packet.SoundUploadedPacket;
import com.zoonie.InteractionSounds.network.packet.client.GetUploadedSoundsPacket;
import com.zoonie.InteractionSounds.network.packet.server.UploadedSoundsPacket;
import com.zoonie.InteractionSounds.sound.Sound;

public class NetworkHelper
{
	public static final int PARTITION_SIZE = 30000;

	public static void sendMessageToPlayer(IMessage message, EntityPlayerMP player)
	{
		InteractionSounds.network.sendTo(message, player);
	}

	public static void sendMessageToAll(IMessage message)
	{
		// Forge sendToAll causing client disconnect in MP.
		Iterator playerList = MinecraftServer.getServer().getConfigurationManager().playerEntityList.iterator();
		while(playerList.hasNext())
		{
			InteractionSounds.network.sendTo(message, (EntityPlayerMP) playerList.next());
		}
	}

	public static void sendMessageToAllAround(IMessage message, TargetPoint tp)
	{
		// Forge sendToAllAround causing client disconnect in MP.
		// Temporary method by techstack on minecraft forge forum.

		for(EntityPlayerMP player : (List<EntityPlayerMP>) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			if(player.dimension == tp.dimension)
			{
				double d4 = tp.x - player.posX;
				double d5 = tp.y - player.posY;
				double d6 = tp.z - player.posZ;

				if(d4 * d4 + d5 * d5 + d6 * d6 < tp.range * tp.range)
				{
					InteractionSounds.network.sendTo(message, player);
				}
			}
		}
	}

	public static void syncPlayerSounds(EntityPlayer player)
	{
		InteractionSounds.network.sendToServer(new GetUploadedSoundsPacket(player));
	}

	public static void syncAllPlayerSounds()
	{
		NetworkHelper.sendMessageToAll(new UploadedSoundsPacket());
	}

	@SideOnly(Side.CLIENT)
	public static void clientSoundUpload(Sound sound)
	{
		sound.setState(Sound.SoundState.UPLOADING);
		uploadSound(sound, Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedTextForChat());
	}

	public static void serverSoundUpload(Sound sound, EntityPlayerMP player)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
		{
			byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
			InteractionSounds.network.sendTo(new SoundChunkPacket(sound.getSoundName(), bytes), player);
		}
		InteractionSounds.network.sendTo(new SoundUploadedPacket(sound.getSoundName(), MinecraftServer.getServer().getMOTD()), player);
	}

	private static void uploadSound(Sound sound, String category)
	{
		byte[] soundBytes = convertFileToByteArr(sound.getSoundLocation());
		for(int i = 0; i < soundBytes.length; i += PARTITION_SIZE)
		{
			byte[] bytes = ArrayUtils.subarray(soundBytes, i, i + Math.min(PARTITION_SIZE, soundBytes.length - i));
			InteractionSounds.network.sendToServer(new SoundChunkPacket(sound.getSoundName(), bytes));
		}
		InteractionSounds.network.sendToServer(new SoundUploadedPacket(sound.getSoundName(), category));
	}

	public static byte[] convertFileToByteArr(File file)
	{
		if(file != null && file.exists())
		{
			try
			{
				return FileUtils.readFileToByteArray(file);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static File createFileFromByteArr(byte[] byteFile, String category, String fileName)
	{
		if(byteFile != null && byteFile.length > 0 && !category.isEmpty() && !fileName.isEmpty())
		{
			File file = new File(SoundHandler.getSoundsFolder().getAbsolutePath() + File.separator + category + File.separator + fileName);
			try
			{
				FileUtils.writeByteArrayToFile(file, byteFile);
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
}
