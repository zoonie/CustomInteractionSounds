package com.zoonie.custominteractionsounds.sound;

import java.util.HashMap;
import java.util.Map;

import com.zoonie.custominteractionsounds.compat.BlockPos;

public class DelayedPlayHandler
{
	private static Map<SoundInfo, SoundPlayInfo> map = new HashMap<SoundInfo, SoundPlayInfo>();

	public static void addDelayedPlay(SoundInfo soundInfo, String identifier, BlockPos pos, float volume, String loop)
	{
		map.put(soundInfo, new SoundPlayInfo(identifier, pos, volume, loop));
	}

	public static void onSoundReceived(SoundInfo soundInfo)
	{
		SoundPlayInfo playInfo = map.get(soundInfo);
		if(playInfo != null)
		{
			BlockPos pos = playInfo.pos;
			SoundHandler.playSound(soundInfo, playInfo.identifier, pos, playInfo.volume);
			if(playInfo.loop != null)
			{
				if(playInfo.loop.equals("left"))
					SoundPlayer.getInstance().setLeftClickLoop(playInfo.identifier, SoundHandler.getSound(soundInfo).getLength(), pos);
				else if(playInfo.loop.equals("right"))
					SoundPlayer.getInstance().setRightClickLoop(playInfo.identifier, SoundHandler.getSound(soundInfo).getLength(), pos);
			}
			map.remove(soundInfo);
		}
	}

	public static void removeSound(String soundName, String category)
	{
		map.remove(new SoundInfo(soundName, category));
	}
}
