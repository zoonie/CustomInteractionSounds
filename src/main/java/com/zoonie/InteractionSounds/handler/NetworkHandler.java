package com.zoonie.InteractionSounds.handler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

public class NetworkHandler
{

	private static Map<String, byte[]> soundChunks = new HashMap<String, byte[]>();

	public static void addSoundChunk(String soundName, String category, byte[] soundChunk)
	{
		if(soundChunks.containsKey(soundName + category))
		{
			soundChunks.put(soundName + category, ArrayUtils.addAll(soundChunks.get(soundName + category), soundChunk));
		}
		else
		{
			soundChunks.put(soundName + category, soundChunk);
		}
	}

	public static byte[] soundUploaded(String soundName, String category)
	{
		return soundChunks.remove(soundName + category);
	}
}
