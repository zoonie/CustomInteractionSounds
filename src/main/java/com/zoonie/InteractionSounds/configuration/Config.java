package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;

public class Config
{
	public static double MaxSoundLength;
	public static long MaxSoundSize;

	public Config(Configuration config)
	{
		MaxSoundLength = config.get("Server Side Settings", "Max sound length in seconds", Double.POSITIVE_INFINITY).getDouble();
		MaxSoundSize = (long) (config.get("Server Side Settings", "Max sound size in bytes", Double.POSITIVE_INFINITY).getDouble());
	}
}
