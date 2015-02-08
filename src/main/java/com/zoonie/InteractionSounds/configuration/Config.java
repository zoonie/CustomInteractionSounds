package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;

public class Config
{
	public static double MaxSoundLength;
	public static long MaxSoundSize;

	public Config(Configuration config)
	{
		MaxSoundLength = config.get("Server Upload Settings", "Max Sound Length", Double.POSITIVE_INFINITY).getDouble();
		MaxSoundSize = (long) config.get("Server Upload Settings", "Max Sound Size", Double.POSITIVE_INFINITY).getDouble();
	}
}
