package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientSettingsConfig
{
	public static boolean soundOverride;

	@SideOnly(Side.CLIENT)
	public ClientSettingsConfig(Configuration config)
	{
		String category = "Client Settings";
		soundOverride = config.get(category, "Mute default block dig sounds when custom sound is being used", true).getBoolean();
	}
}
