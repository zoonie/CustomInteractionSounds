package com.zoonie.interactionsounds.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

import com.zoonie.interactionsounds.InteractionSounds;
import com.zoonie.interactionsounds.configuration.ClientConfigHandler;

public class ModGuiConfig extends GuiConfig
{

	public ModGuiConfig(GuiScreen parentScreen)
	{
		super(parentScreen, new ConfigElement(ClientConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), InteractionSounds.MODID, false, false,
				GuiConfig.getAbridgedConfigPath(ClientConfigHandler.config.toString()));
	}

}
