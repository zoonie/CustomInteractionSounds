package com.zoonie.custominteractionsounds.network;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.zoonie.custominteractionsounds.sound.SoundInfo;

public class NetworkHandler
{
	public static Multimap<SoundInfo, EntityPlayerMP> waiting = ArrayListMultimap.create();

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
