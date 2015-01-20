package com.zoonie.InteractionSounds.proxy;

import java.io.File;
import java.util.HashMap;

import javax.swing.UIManager;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.configuration.ConfigurationManager;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.handler.event.InteractionHandler;
import com.zoonie.InteractionSounds.handler.event.SoundEventHandler;
import com.zoonie.InteractionSounds.sound.Sound;

public class ClientProxy extends CommonProxy
{
	public static KeyBinding recordInteraction;
	public static HashMap<Interaction, Sound> mappings = new HashMap<Interaction, Sound>();
	private static ConfigurationManager config;

	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(new InteractionHandler());

		recordInteraction = new KeyBinding("Record Interaction", Keyboard.KEY_R, InteractionSounds.MOD_NAME);
		ClientRegistry.registerKeyBinding(recordInteraction);
	}

	@Override
	public void UISetup()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void soundSetup()
	{
		super.soundSetup();

		MinecraftForge.EVENT_BUS.register(new SoundEventHandler());

		SoundHandler.getSounds();
	}

	@Override
	public void configSetup(File file)
	{
		config = new ConfigurationManager(new File(file, InteractionSounds.MODID + ".cfg"));
	}

	@Override
	public ConfigurationManager getConfig()
	{
		return config;
	}
}
