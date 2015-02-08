package com.zoonie.InteractionSounds.proxy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zoonie.InteractionSounds.configuration.Config;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		configSetup(event.getSuggestedConfigurationFile());
	}

	private void configSetup(File file)
	{
		Configuration config = new Configuration(file);
		new Config(config);
		config.save();
	}
}
