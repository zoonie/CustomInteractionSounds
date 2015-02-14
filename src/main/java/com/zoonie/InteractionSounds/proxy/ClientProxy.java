package com.zoonie.InteractionSounds.proxy;

import java.io.File;

import javax.swing.UIManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.interaction.InteractionHandler;
import com.zoonie.InteractionSounds.interaction.KeyBindings;
import com.zoonie.InteractionSounds.interaction.KeyInputHandler;
import com.zoonie.InteractionSounds.interaction.TickHandler;
import com.zoonie.InteractionSounds.network.ClientConnectionHandler;
import com.zoonie.InteractionSounds.sound.SoundEventHandler;
import com.zoonie.InteractionSounds.sound.SoundHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		handlerSetup();
		UISetup();
		soundSetup();
		configSetup();
	}

	private void handlerSetup()
	{
		MinecraftForge.EVENT_BUS.register(InteractionHandler.getInstance());
		FMLCommonHandler.instance().bus().register(new TickHandler());
		FMLCommonHandler.instance().bus().register(new ClientConnectionHandler());

		FMLCommonHandler.instance().bus().register(new KeyInputHandler());
		KeyBindings.init();
	}

	private void UISetup()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void soundSetup()
	{
		MinecraftForge.EVENT_BUS.register(new SoundEventHandler());

		SoundHandler.getSounds();
	}

	private void configSetup()
	{
		new MappingsConfigManager(new File("sounds" + File.separator + InteractionSounds.MOD_NAME + File.separator + InteractionSounds.MODID + ".json"));
	}
}
