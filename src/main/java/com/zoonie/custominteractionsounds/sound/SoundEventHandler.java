package com.zoonie.custominteractionsounds.sound;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;

import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.cuina.fireandfuel.CodecJLayerMP3;

public class SoundEventHandler
{
	@SubscribeEvent
	public void onSoundSetup(SoundSetupEvent event)
	{
		try
		{
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setCodec("mp3", CodecJLayerMP3.class);
		} catch(SoundSystemException e)
		{
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void onSoundLoad(SoundLoadEvent event)
	{
		ClientConfigHandler.load();
	}
}
