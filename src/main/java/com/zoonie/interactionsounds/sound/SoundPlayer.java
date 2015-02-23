package com.zoonie.interactionsounds.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

import com.zoonie.interactionsounds.network.ChannelHandler;
import com.zoonie.interactionsounds.network.message.RepeatSoundMessage;

@SideOnly(Side.CLIENT)
public class SoundPlayer
{
	private static SoundPlayer instance = new SoundPlayer();

	private SoundSystem soundSystem;
	private ArrayList<String> playing = new ArrayList<String>();
	private Loop leftClickLoop = null;
	private Loop rightClickLoop = null;

	private void init()
	{
		if(soundSystem == null || soundSystem.randomNumberGenerator == null)
		{
			SoundManager soundManager = ObfuscationReflectionHelper.getPrivateValue(net.minecraft.client.audio.SoundHandler.class, Minecraft.getMinecraft().getSoundHandler(), "sndManager",
					"field_147694_f", "V");
			soundSystem = ObfuscationReflectionHelper.getPrivateValue(SoundManager.class, soundManager, "sndSystem", "field_148620_e", "e");
		}
	}

	public String playNewSound(File sound, String id, BlockPos pos, boolean fading, float volume)
	{
		try
		{
			init();

			String identifier;
			if(id == null)
				identifier = UUID.randomUUID().toString();
			else
				identifier = id;

			double soundLength = SoundHelper.getSoundLength(sound);
			volume *= Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.PLAYERS);

			if(soundLength > 5)
				soundSystem.newStreamingSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, pos.getX(), pos.getY(), pos.getZ(), fading ? 2 : 0, 16);
			else
				soundSystem.newSource(false, identifier, sound.toURI().toURL(), sound.getName(), false, pos.getX(), pos.getY(), pos.getZ(), fading ? 2 : 0, 16);

			soundSystem.setVolume(identifier, volume);
			soundSystem.play(identifier);

			double timeToEnd = getTimeToEnd(soundLength);
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

	public void setLeftClickLoop(String identifier, double soundLength, BlockPos pos)
	{
		double timeToEnd = getTimeToEnd(soundLength);
		leftClickLoop = new Loop(identifier, soundLength, timeToEnd, pos);
	}
	
	public void setRightClickLoop(String identifier, double soundLength, BlockPos pos)
	{
		double timeToEnd = getTimeToEnd(soundLength);
		rightClickLoop = new Loop(identifier, soundLength, timeToEnd, pos);
	}

	public void updateLeftClickLoop()
	{
		updateLoop(leftClickLoop);
	}

	public void updateRightClickLoop()
	{
		updateLoop(rightClickLoop);
	}

	private void updateLoop(Loop loop)
	{
		if(loop == null)
			return;

		EntityPlayer player = Minecraft.getMinecraft().thePlayer;

		if(loop.timeToEnd < System.currentTimeMillis())
		{
			playSound(loop.identifier, (float) player.posX, (float) player.posY, (float) player.posZ);

			ChannelHandler.network.sendToServer(new RepeatSoundMessage(loop.identifier, player.dimension, (int) player.posX, (int) player.posY, (int) player.posZ, player
					.getDisplayNameString()));
			double timeToEnd = getTimeToEnd(loop.length);
			loop.timeToEnd = timeToEnd;
		}
	}

	public void cleanUp()
	{
		ArrayList<String> temp = new ArrayList<String>(playing);
		for(String s : playing)
		{
			boolean notLeftLoop = (leftClickLoop == null || !leftClickLoop.identifier.equals(s)) ? true : false;
			if(!soundSystem.playing(s) && (leftClickLoop == null || !leftClickLoop.identifier.equals(s)) && (rightClickLoop == null || !rightClickLoop.identifier.equals(s)))
			{
				soundSystem.removeSource(s);
				temp.remove(s);
			}
		}
		playing = temp;
	}

	public void stopLeftClickLoop()
	{
		leftClickLoop = null;
	}

	public void stopRightClickLoop()
	{
		rightClickLoop = null;
	}

	public void adjustVolume(String identifier, float volume)
	{
		init();
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

	private double getTimeToEnd(double length)
	{
		return System.currentTimeMillis() + length;
	}

	public static SoundPlayer getInstance()
	{
		return instance;
	}
}
