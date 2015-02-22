package com.zoonie.interactionsounds.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.zoonie.interactionsounds.configuration.MappingsConfigManager;
import com.zoonie.interactionsounds.configuration.ServerSettingsConfig;
import com.zoonie.interactionsounds.interaction.KeyBindings;
import com.zoonie.interactionsounds.sound.SoundHandler;
import com.zoonie.interactionsounds.sound.SoundPlayer;

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
