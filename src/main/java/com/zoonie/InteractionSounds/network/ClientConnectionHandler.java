package com.zoonie.InteractionSounds.network;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

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
	}
}
