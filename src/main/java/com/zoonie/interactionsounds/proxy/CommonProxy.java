package com.zoonie.interactionsounds.proxy;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zoonie.interactionsounds.InteractionSounds;
import com.zoonie.interactionsounds.configuration.MappingsConfigManager;
import com.zoonie.interactionsounds.configuration.ServerSettingsConfig;
import com.zoonie.interactionsounds.network.ServerHandler;

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
		new MappingsConfigManager(new File("sounds" + File.separator + InteractionSounds.MOD_NAME + File.separator + InteractionSounds.MODID + ".json"));
	}
}
