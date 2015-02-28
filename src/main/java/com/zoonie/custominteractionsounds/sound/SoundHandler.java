package com.zoonie.custominteractionsounds.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.io.Files;
import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.network.ChannelHandler;
import com.zoonie.custominteractionsounds.network.message.RequestSoundMessage;

public class SoundHandler
{
	private static File soundsFolder;
	private static TreeMap<SoundInfo, Sound> sounds;

	public static File getSoundsFolder()
	{
		if(soundsFolder == null)
		{
			findSounds();
		}
		return soundsFolder;
	}

	public static TreeMap<SoundInfo, Sound> getSounds()
	{
		if(sounds == null)
			findSounds();

		return sounds;
	}

	public static void reloadSounds()
	{
		sounds = null;
		getSounds();
	}

	public static Sound getSound(SoundInfo soundInfo)
	{
		if(sounds == null)
			findSounds();

		return sounds.get(soundInfo);
	}

	public static ArrayList<Sound> getPlayerSounds()
	{
		if(sounds == null)
			findSounds();

		String player = Minecraft.getMinecraft().thePlayer.getDisplayNameString();
		ArrayList<Sound> soundsList = new ArrayList<Sound>();

		for(SoundInfo soundInfo : sounds.keySet())
		{
			if(soundInfo.category.equals(player))
				soundsList.add(sounds.get(soundInfo));
		}

		return soundsList;
	}

	public static void findSounds()
	{
		soundsFolder = new File("sounds");
		if(!soundsFolder.exists())
		{
			soundsFolder.mkdir();
		}
		sounds = new TreeMap<SoundInfo, Sound>();
		addSoundsFromDir(soundsFolder);
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
					sounds.put(new SoundInfo(sound.getSoundName(), sound.getCategory()), sound);
				}
			}
			else if(file.isDirectory())
			{
				addSoundsFromDir(file);
			}
		}
	}

	public static void addSound(SoundInfo soundInfo, File soundFile)
	{
		Sound sound = getSound(soundInfo);
		if(sound != null)
		{
			if(sound.getState() != Sound.SoundState.SYNCED)
			{
				sound.onSoundDownloaded(soundFile);
			}
		}
		else
		{
			sounds.put(soundInfo, new Sound(soundFile));
		}
	}

	public static void addRemoteSound(SoundInfo soundInfo)
	{
		Sound sound = getSound(soundInfo);
		if(sound != null)
		{
			if(sound.hasLocal())
			{
				sound.onSoundUploaded();
			}
		}
		else
		{
			sounds.put(soundInfo, new Sound(soundInfo));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playSound(SoundInfo soundInfo, String identifier, BlockPos pos, float volume)
	{
		Sound sound = SoundHandler.getSound(soundInfo);
		if(sound != null && sound.hasLocal())
		{
			SoundPlayer.getInstance().playNewSound(sound.getSoundLocation(), identifier, pos, true, volume);
		}
		else
		{
			sounds.put(soundInfo, new Sound(soundInfo));
			DelayedPlayHandler.addDelayedPlay(soundInfo, identifier, pos, volume, null);
			ChannelHandler.network.sendToServer(new RequestSoundMessage(soundInfo.name, soundInfo.category));
		}
	}

	@SideOnly(Side.CLIENT)
	public static Sound setupSound(File file)
	{
		File category = new File("sounds" + File.separator + CustomInteractionSounds.MOD_NAME + File.separator + Minecraft.getMinecraft().thePlayer.getDisplayNameString());
		if(!category.exists())
		{
			category.mkdirs();
		}

		File newFile = new File(category.getAbsolutePath() + File.separator + file.getName());
		try
		{
			Files.copy(file, newFile);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return new Sound(newFile);
	}
}
