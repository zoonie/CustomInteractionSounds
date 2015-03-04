package com.zoonie.custominteractionsounds.proxy;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.configuration.MappingsConfigManager;
import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.network.ServerHandler;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		handlerSetup();
		configSetup(event.getSuggestedConfigurationFile());
		mappingsConfigSetup();
	}

	private void handlerSetup()
	{
		MinecraftForge.EVENT_BUS.register(new ServerHandler());
	}

	public void configSetup(File file)
	{
		Configuration config = new Configuration(file);
		new ServerSettingsConfig(config);
		config.save();
	}

	public void mappingsConfigSetup()
	{
		new MappingsConfigManager(new File("sounds" + File.separator + CustomInteractionSounds.MOD_NAME + File.separator + CustomInteractionSounds.MODID + ".json"));
	}
}
