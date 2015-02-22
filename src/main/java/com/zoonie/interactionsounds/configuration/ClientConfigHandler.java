package com.zoonie.interactionsounds.configuration;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zoonie.interactionsounds.InteractionSounds;

public class ClientConfigHandler
{
	public static boolean soundOverride;
	public static boolean muteOthers;
	public static Configuration config;

	@SideOnly(Side.CLIENT)
	public ClientConfigHandler(Configuration configParam)
	{
		config = configParam;
		load();
	}

	public static void load()
	{
		soundOverride = config.get(Configuration.CATEGORY_GENERAL, "Mute default block dig sounds", true, I18n.format("config.mute.dig")).getBoolean();
		muteOthers = config.get(Configuration.CATEGORY_GENERAL, "Mute other players' sounds", false, I18n.format("config.mute.others")).getBoolean();

		config.save();
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent event)
	{
		if(event.modID.equalsIgnoreCase(InteractionSounds.MODID))
			load();
	}
}
