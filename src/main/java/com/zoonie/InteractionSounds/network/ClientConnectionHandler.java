package com.zoonie.InteractionSounds.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.zoonie.InteractionSounds.configuration.Config;
import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.interaction.KeyBindings;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class ClientConnectionHandler
{
	@SubscribeEvent
	public void connect(ClientConnectedToServerEvent event)
	{
		SoundPlayer.getInstance().init();
	}

	@SubscribeEvent
	public void disconnect(ClientDisconnectionFromServerEvent event)
	{
		SoundPlayer.getInstance().stopSounds();
		MappingsConfigManager.read();
		SoundHandler.reloadSounds();
		if(Config.UseServerMappings)
		{
			KeyBindings.assign();
			Config.UseServerMappings = false;
		}
	}
}
