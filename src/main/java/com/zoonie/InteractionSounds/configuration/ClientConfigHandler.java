package com.zoonie.InteractionSounds.configuration;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zoonie.InteractionSounds.InteractionSounds;

public class ClientConfigHandler
{
	public static final String category = "Client Settings";

	public static boolean soundOverride;
	public static boolean muteOthers;
	public static Configuration config;

	@SideOnly(Side.CLIENT)
	public ClientConfigHandler(Configuration configParam)
	{
		config = configParam;
		load();
	}

	private void load()
	{
		soundOverride = config.get(category, "Mute default block dig sounds", true).getBoolean();
		muteOthers = config.get(category, "Mute other players' sounds", false).getBoolean();

		config.save();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event)
	{
		if(event.modID.equalsIgnoreCase(InteractionSounds.MODID))
			load();
	}
}
