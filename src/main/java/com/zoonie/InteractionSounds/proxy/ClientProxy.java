package com.zoonie.InteractionSounds.proxy;

import java.io.File;
import java.util.HashMap;

import javax.swing.UIManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.handler.event.InteractionHandler;
import com.zoonie.InteractionSounds.handler.event.KeyBindings;
import com.zoonie.InteractionSounds.handler.event.KeyInputHandler;
import com.zoonie.InteractionSounds.handler.event.SoundEventHandler;
import com.zoonie.InteractionSounds.sound.Sound;

public class ClientProxy extends CommonProxy
{
	public static HashMap<Interaction, Sound> mappings = new HashMap<Interaction, Sound>();

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		inputHandlerSetup();
		UISetup();
		soundSetup();
		configSetup();
	}

	private void inputHandlerSetup()
	{
		MinecraftForge.EVENT_BUS.register(new InteractionHandler());
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
