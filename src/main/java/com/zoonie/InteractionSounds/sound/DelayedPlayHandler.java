package com.zoonie.InteractionSounds.sound;

import java.util.HashMap;
import java.util.Map;

public class DelayedPlayHandler
{
	private static Map<SoundInfo, SoundPlayInfo> map = new HashMap<SoundInfo, SoundPlayInfo>();

	public static void addDelayedPlay(SoundInfo soundInfo, String identifier, int x, int y, int z, float volume, boolean loop)
	{
		map.put(soundInfo, new SoundPlayInfo(identifier, x, y, z, volume, loop));
	}

	public static void onSoundReceived(SoundInfo soundInfo)
	{
		SoundPlayInfo playInfo = map.get(soundInfo);
		if(playInfo != null)
		{
			SoundHandler.playSound(soundInfo, playInfo.identifier, playInfo.x, playInfo.y, playInfo.z, playInfo.volume);
			if(playInfo.loop)
				SoundPlayer.getInstance().addLoop(playInfo.identifier, SoundHandler.getSound(soundInfo).getLength());
			map.remove(soundInfo);
		}
	}

	public static void removeSound(String soundName, String category)
	{
		map.remove(new SoundInfo(soundName, category));
	}
}
