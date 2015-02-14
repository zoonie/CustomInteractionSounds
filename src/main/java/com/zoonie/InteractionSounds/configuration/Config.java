package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Config
{
	public static double MaxSoundLength = Double.POSITIVE_INFINITY;
	public static boolean UseServerMappings = false;

	@SideOnly(Side.SERVER)
	public Config(Configuration config)
	{
		String category = "Server Side Settings";
		MaxSoundLength = config.get(category, "Max sound length in seconds", Double.POSITIVE_INFINITY).getDouble();
		UseServerMappings = config.get(category, "Force server's interaction->sound mappings to players", false).getBoolean();
	}
}
