package com.zoonie.InteractionSounds.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public class SoundPlayer
{
	private static SoundPlayer instance = new SoundPlayer();

	private SoundSystem soundSystem;
	private final int SIZE = 100;
	private HashMap<String, Double> playing = new HashMap<String, Double>();
	private int index = 0;

	private void init()
	{
		SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager",
				"field_147694_f", "V");
		soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
	}

	public String playSound(File sound, float x, float y, float z, boolean fading, float volume)
	{
		if(soundSystem == null)
		{
			init();
		}
		try
		{
			String identifier;
			double soundLength = SoundHelper.getSoundLength(sound);

			if(soundLength > 5)
			{
				identifier = soundSystem.quickStream(false, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);
			}
			else
				identifier = soundSystem.quickPlay(false, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);

			double timeToEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long) soundLength);
			playing.put(identifier, timeToEnd);

			volume *= Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
			soundSystem.setVolume(identifier, volume);

			return identifier;
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void stopSound(String identifier)
	{
		if(identifier != null)
			soundSystem.stop(identifier);
	}

	public void removeSound(String identifier)
	{
		playing.remove(identifier);
	}

	public void adjustVolume(String identifier, float volume)
	{
		soundSystem.setVolume(identifier, volume);
	}

	public void stopSounds()
	{
		if(soundSystem != null)
		{
			Iterator<Entry<String, Double>> playingList = playing.entrySet().iterator();
			while(playingList.hasNext())
			{
				Entry<String, Double> e = playingList.next();
				if(e.getValue() > System.currentTimeMillis())
					stopSound(e.getKey());
			}
		}
		playing = new HashMap<String, Double>();
		soundSystem = null;
	}

	public static SoundPlayer getInstance()
	{
		return instance;
	}
}
