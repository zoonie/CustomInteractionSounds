package com.zoonie.InteractionSounds.handler;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.io.Files;
import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.network.packet.client.CheckPresencePacket;
import com.zoonie.InteractionSounds.network.packet.server.SoundRemovedPacket;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class SoundHandler
{
	private static File soundsFolder;
	private static LinkedHashMap<String, Sound> sounds;

	public static File getSoundsFolder()
	{
		if(soundsFolder == null)
		{
			findSounds();
		}
		return soundsFolder;
	}

	public static LinkedHashMap<String, Sound> getSounds()
	{
		if(sounds == null)
		{
			findSounds();
		}
		return sounds;
	}

	public static Sound getSound(String fileName, String category)
	{
		if(sounds == null)
		{
			findSounds();
		}
		return sounds.get(fileName + category);
	}

	public static void findSounds()
	{
		soundsFolder = new File("sounds");
		if(!soundsFolder.exists())
		{
			soundsFolder.mkdir();
		}
		sounds = new LinkedHashMap<String, Sound>();
		addSoundsFromDir(soundsFolder);
	}

	public static void removeSound(Sound sound)
	{
		if(sound != null)
		{
			if(!sound.getSoundLocation().delete())
			{
				sound.getSoundLocation().deleteOnExit();
			}
			sounds.remove(sound.getSoundName() + sound.getCategory());
			if(FMLCommonHandler.instance().getEffectiveSide().isServer())
			{
				NetworkHelper.sendMessageToAll(new SoundRemovedPacket(sound.getSoundName()));
			}
		}
	}

	private static void addSoundsFromDir(File dir)
	{
		for(File file : dir.listFiles())
		{
			if(file.isFile())
			{
				if(file.getName().endsWith(".ogg") || file.getName().endsWith(".wav") || file.getName().endsWith(".mp3"))
				{
					Sound sound = new Sound(file);
					sounds.put(sound.getSoundName() + sound.getCategory(), sound);
				}
			}
			else if(file.isDirectory())
			{
				addSoundsFromDir(file);
			}
		}
	}

	public static void addSound(String soundName, String category, File soundFile)
	{
		Sound sound = getSound(soundName, category);
		if(sound != null)
		{
			if(sound.getState() != Sound.SoundState.SYNCED)
			{
				sound.onSoundDownloaded(soundFile);
			}
		}
		else
		{
			sounds.put(soundName + category, new Sound(soundFile));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(String soundName, String category, String identifier, int x, int y, int z, float volume)
	{
		Sound sound = SoundHandler.getSound(soundName, category);
		if(sound != null)
		{
			if(sound.hasLocal())
				SoundPlayer.playSound(sound.getSoundLocation(), identifier, x, y, z, true, volume);
		}
		else
		{
			sounds.put(soundName + category, new Sound(soundName, category));
			DelayedPlayHandler.addDelayedPlay(soundName, category, identifier, x, y, z, volume);
			ChannelHandler.network.sendToServer(new CheckPresencePacket(soundName, category));
		}
	}

	@SideOnly(Side.CLIENT)
	public static Sound setupSound(File file)
	{
		File category = new File("sounds" + File.separator + "Interaction Sounds" + File.separator + Minecraft.getMinecraft().thePlayer.getDisplayNameString());
		if(!category.exists())
		{
			category.mkdirs();
		}

		File newFile = new File(category.getAbsolutePath() + File.separator + file.getName());
		try
		{
			Files.copy(file, newFile);
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		return new Sound(newFile);
	}
}
