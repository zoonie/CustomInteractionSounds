package com.zoonie.InteractionSounds.proxy;

import java.io.File;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		configSetup(event.getSuggestedConfigurationFile());
	}

	private void configSetup(File config)
	{

	}
}
