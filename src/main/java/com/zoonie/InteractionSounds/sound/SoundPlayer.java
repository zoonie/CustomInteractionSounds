package com.zoonie.InteractionSounds.sound;

import java.io.File;
import java.net.MalformedURLException;

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
	private static SoundSystem soundSystem;

	private static final int SIZE = 200;
	private static String[] playing = new String[SIZE];
	private static int index = 0;

	private static void init()
	{
		SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager",
				"field_147694_f", "V");
		soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
	}

	public static void playSound(File sound, String identifier, float x, float y, float z, boolean fading, float volume)
	{
		if(soundSystem == null)
		{
			init();
		}
		try
		{
			playing[index++ % SIZE] = identifier;

			soundSystem.newSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);

			volume *= Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);
			soundSystem.setVolume(identifier, volume);

			soundSystem.play(identifier);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
	}

	public static void stopSound(String identifier)
	{
		if(identifier != null)
			soundSystem.stop(identifier);
	}

	public static void adjustVolume(String identifier, float volume)
	{
		soundSystem.setVolume(identifier, volume);
	}

	public static void stopSounds()
	{
		if(soundSystem != null)
		{
			for(int i = 0; i < SIZE; i++)
			{
				stopSound(playing[i]);
			}
		}
	}
}
