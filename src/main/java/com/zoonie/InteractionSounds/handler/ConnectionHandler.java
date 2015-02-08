package com.zoonie.InteractionSounds.handler;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class ConnectionHandler
{
	@SubscribeEvent
	public void disconnect(ClientDisconnectionFromServerEvent event)
	{
		SoundPlayer.stopSounds();
	}
}