package com.zoonie.InteractionSounds.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.configuration.ServerSettingsConfig;
import com.zoonie.InteractionSounds.interaction.KeyBindings;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class ClientConnectionHandler
{
	@SubscribeEvent
	public void disconnect(ClientDisconnectionFromServerEvent event)
	{
		SoundPlayer.getInstance().stopSounds();
		MappingsConfigManager.read();
		SoundHandler.reloadSounds();
		if(ServerSettingsConfig.UseServerMappings)
		{
			KeyBindings.assign();
			ServerSettingsConfig.UseServerMappings = false;
		}
		ServerSettingsConfig.MaxSoundLength = Double.POSITIVE_INFINITY;
	}
}
