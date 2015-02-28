package com.zoonie.custominteractionsounds.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;

public class ModGuiConfig extends GuiConfig
{

	public ModGuiConfig(GuiScreen parentScreen)
	{
		super(parentScreen, new ConfigElement(ClientConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), CustomInteractionSounds.MODID, false, false,
				GuiConfig.getAbridgedConfigPath(ClientConfigHandler.config.toString()));
	}

}
