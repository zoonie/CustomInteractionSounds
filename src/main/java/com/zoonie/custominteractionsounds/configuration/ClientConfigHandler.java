package com.zoonie.custominteractionsounds.configuration;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.Configuration;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		if(event.modID.equalsIgnoreCase(CustomInteractionSounds.MODID))
			load();
	}
}
