package com.zoonie.custominteractionsounds.proxy;

import javax.swing.UIManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;
import com.zoonie.custominteractionsounds.interaction.InteractionHandler;
import com.zoonie.custominteractionsounds.interaction.KeyBindings;
import com.zoonie.custominteractionsounds.interaction.KeyInputHandler;
import com.zoonie.custominteractionsounds.interaction.TickHandler;
import com.zoonie.custominteractionsounds.network.ClientConnectionHandler;
import com.zoonie.custominteractionsounds.sound.SoundEventHandler;
import com.zoonie.custominteractionsounds.sound.SoundHandler;

public class ClientProxy extends CommonProxy
{
	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		handlerSetup();
		UISetup();
		soundSetup();
		FMLCommonHandler.instance().bus().register(new ClientConfigHandler(new Configuration(event.getSuggestedConfigurationFile())));
		super.mappingsConfigSetup();
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
}
