package com.zoonie.custominteractionsounds.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;

import cpw.mods.fml.client.config.GuiConfig;

public class ModGuiConfig extends GuiConfig
{

	public ModGuiConfig(GuiScreen parentScreen)
	{
		super(parentScreen, new ConfigElement(ClientConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), CustomInteractionSounds.MODID, false, false,
				GuiConfig.getAbridgedConfigPath(ClientConfigHandler.config.toString()));
	}

}
