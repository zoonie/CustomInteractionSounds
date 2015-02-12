package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Config
{
	public static double MaxSoundLength = Double.POSITIVE_INFINITY;
	public static long MaxSoundSize = Long.MAX_VALUE;

	@SideOnly(Side.SERVER)
	public Config(Configuration config)
	{
		MaxSoundLength = config.get("Server Side Settings", "Max sound length in seconds", Double.POSITIVE_INFINITY).getDouble();
		MaxSoundSize = (long) (config.get("Server Side Settings", "Max sound size in bytes", Double.POSITIVE_INFINITY).getDouble());
	}
}
