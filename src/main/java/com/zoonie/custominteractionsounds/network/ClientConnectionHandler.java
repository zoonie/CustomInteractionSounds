package com.zoonie.custominteractionsounds.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.zoonie.custominteractionsounds.configuration.MappingsConfigManager;
import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.interaction.KeyBindings;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundPlayer;

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
