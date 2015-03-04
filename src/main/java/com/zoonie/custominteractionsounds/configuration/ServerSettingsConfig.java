package com.zoonie.custominteractionsounds.configuration;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ServerSettingsConfig
{
	public static double MaxSoundLength = Double.POSITIVE_INFINITY;
	public static boolean UseServerMappings = false;

	@SideOnly(Side.SERVER)
	public ServerSettingsConfig(Configuration config)
	{
		String category = "Server Settings";
		MaxSoundLength = config.get(category, "Max sound length in seconds", Double.POSITIVE_INFINITY).getDouble();
		UseServerMappings = config.get(category, "Force server's interaction->sound mappings to players", false).getBoolean();
	}
}
