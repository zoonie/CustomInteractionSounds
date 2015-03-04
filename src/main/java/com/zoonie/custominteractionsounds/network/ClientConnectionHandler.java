package com.zoonie.custominteractionsounds.network;

import com.zoonie.custominteractionsounds.configuration.MappingsConfigManager;
import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.interaction.KeyBindings;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

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
