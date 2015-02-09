package com.zoonie.InteractionSounds.sound;

import java.util.HashMap;
import java.util.Map;

public class DelayedPlayHandler
{
	private static Map<String, SoundPlayInfo> map = new HashMap<String, SoundPlayInfo>();

	public static void addDelayedPlay(String soundName, String category, int x, int y, int z, float volume)
	{
		map.put(soundName + category, new SoundPlayInfo(x, y, z, volume));
	}

	public static void onSoundReceived(String soundName, String category)
	{
		SoundPlayInfo info = map.get(soundName + category);
		if(info != null)
		{
			SoundHandler.playSound(soundName, category, info.x, info.y, info.z, info.volume);
			map.remove(soundName + category);
		}
	}

	public static void removeSound(String soundName, String category)
	{
		map.remove(soundName + category);
	}
}
