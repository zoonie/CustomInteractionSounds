package com.zoonie.InteractionSounds.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.network.message.RepeatSoundMessage;

@SideOnly(Side.CLIENT)
public class SoundPlayer
{
	private static SoundPlayer instance = new SoundPlayer();

	private SoundSystem soundSystem;
	private ArrayList<String> playing = new ArrayList<String>();
	private HashMap<String, Entry<Double, Double>> loops = new HashMap<String, Entry<Double, Double>>();

	private void init()
	{
		SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager",
				"field_147694_f", "V");
		soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
	}

	public String playNewSound(File sound, String id, float x, float y, float z, boolean fading, float volume)
	{
		try
		{
			if(soundSystem == null || soundSystem.randomNumberGenerator == null)
			{
				init();
			}
			String identifier;
			if(id == null)
				identifier = UUID.randomUUID().toString();
			else
				identifier = id;

			double soundLength = SoundHelper.getSoundLength(sound);
			volume *= Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);

			if(soundLength > 5)
				soundSystem.newStreamingSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);
			else
				soundSystem.newSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, x, y, z, fading ? 2 : 0, 16);

			soundSystem.setVolume(identifier, volume);
			soundSystem.play(identifier);

			double timeToEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long) soundLength);
			playing.add(identifier);

			return identifier;
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public void playSound(String identifier, float x, float y, float z)
	{
		if(playing.contains(identifier))
		{
			soundSystem.setPosition(identifier, x, y, z);
			soundSystem.play(identifier);
		}
	}

	public void stopSound(String identifier)
	{
		soundSystem.stop(identifier);
		removeSound(identifier);
	}

	private void removeSound(String identifier)
	{
		playing.remove(identifier);
		soundSystem.removeSource(identifier);
	}

	public void addLoop(String identifier, double soundLength)
	{
		double timeToEnd = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis((long) soundLength);
		loops.put(identifier, new SimpleEntry<Double, Double>(soundLength, timeToEnd));
	}

	public void updateLooping()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		HashMap<String, Entry<Double, Double>> newLoops = new HashMap<String, Entry<Double, Double>>();
		for(Entry<String, Entry<Double, Double>> entry : loops.entrySet())
		{
			if(entry.getValue().getValue() < System.currentTimeMillis())
			{
				playSound(entry.getKey(), (float) player.posX, (float) player.posY, (float) player.posZ);

				ChannelHandler.network.sendToServer(new RepeatSoundMessage(entry.getKey(), player.dimension, (int) player.posX, (int) player.posY, (int) player.posZ, player.getDisplayNameString()));
				double timeToEnd = (double) System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(entry.getValue().getKey().longValue());
				newLoops.put(entry.getKey(), new SimpleEntry<Double, Double>(entry.getValue().getKey(), timeToEnd));
			}
		}
		loops.putAll(newLoops);
	}

	public void cleanUp()
	{
		ArrayList<String> temp = new ArrayList<String>(playing);
		for(String s : playing)
		{
			if(!soundSystem.playing(s) && !loops.containsKey(s))
			{
				soundSystem.removeSource(s);
				temp.remove(s);
			}
		}
		playing = temp;
	}

	public void stopLooping(String identifier)
	{
		loops.remove(identifier);
	}

	public void stopAllLooping()
	{
		loops = new HashMap<String, Entry<Double, Double>>();
	}

	public void adjustVolume(String identifier, float volume)
	{
		if(soundSystem.playing(identifier))
		{
			soundSystem.setVolume(identifier, volume);
		}
	}

	public void stopSounds()
	{
		if(soundSystem != null)
		{
			for(String s : playing)
			{
				soundSystem.stop(s);
			}
		}
		playing = new ArrayList<String>();
	}

	public void pauseSounds()
	{
		if(soundSystem != null)
		{
			for(String s : playing)
			{
				soundSystem.pause(s);
			}
		}
	}

	public void resumeSounds()
	{
		if(soundSystem != null)
		{
			for(String s : playing)
			{
				soundSystem.play(s);
			}
		}
	}

	public static SoundPlayer getInstance()
	{
		return instance;
	}
}
