package com.zoonie.InteractionSounds.proxy;

import java.io.File;

import net.minecraftforge.fml.relauncher.Side;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.configuration.ConfigurationManager;
import com.zoonie.InteractionSounds.network.packet.SoundChunkPacket;
import com.zoonie.InteractionSounds.network.packet.SoundUploadedPacket;
import com.zoonie.InteractionSounds.network.packet.client.CheckPresencePacket;
import com.zoonie.InteractionSounds.network.packet.client.ClientPlaySoundMessage;
import com.zoonie.InteractionSounds.network.packet.client.GetUploadedSoundsPacket;
import com.zoonie.InteractionSounds.network.packet.client.RemoveSoundPacket;
import com.zoonie.InteractionSounds.network.packet.server.ServerPlaySoundPacket;
import com.zoonie.InteractionSounds.network.packet.server.SoundNotFoundPacket;
import com.zoonie.InteractionSounds.network.packet.server.SoundReceivedPacket;
import com.zoonie.InteractionSounds.network.packet.server.SoundRemovedPacket;
import com.zoonie.InteractionSounds.network.packet.server.StopSoundPacket;
import com.zoonie.InteractionSounds.network.packet.server.UploadedSoundsPacket;

public class CommonProxy
{
	private static ConfigurationManager config;

	public void registerMessages()
	{
		InteractionSounds.network.registerMessage(CheckPresencePacket.Handler.class, CheckPresencePacket.class, 0, Side.SERVER);
		InteractionSounds.network.registerMessage(GetUploadedSoundsPacket.Handler.class, GetUploadedSoundsPacket.class, 1, Side.SERVER);
		InteractionSounds.network.registerMessage(RemoveSoundPacket.Handler.class, RemoveSoundPacket.class, 2, Side.SERVER);
		InteractionSounds.network.registerMessage(ClientPlaySoundMessage.Handler.class, ClientPlaySoundMessage.class, 3, Side.SERVER);

		InteractionSounds.network.registerMessage(ServerPlaySoundPacket.Handler.class, ServerPlaySoundPacket.class, 6, Side.CLIENT);
		InteractionSounds.network.registerMessage(SoundNotFoundPacket.Handler.class, SoundNotFoundPacket.class, 7, Side.CLIENT);
		InteractionSounds.network.registerMessage(SoundReceivedPacket.Handler.class, SoundReceivedPacket.class, 8, Side.CLIENT);
		InteractionSounds.network.registerMessage(SoundRemovedPacket.Handler.class, SoundRemovedPacket.class, 9, Side.CLIENT);
		InteractionSounds.network.registerMessage(StopSoundPacket.Handler.class, StopSoundPacket.class, 10, Side.CLIENT);
		InteractionSounds.network.registerMessage(UploadedSoundsPacket.Handler.class, UploadedSoundsPacket.class, 11, Side.CLIENT);

		InteractionSounds.network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.CLIENT);
		InteractionSounds.network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.SERVER);

		InteractionSounds.network.registerMessage(SoundUploadedPacket.Handler.class, SoundUploadedPacket.class, 13, Side.CLIENT);
		InteractionSounds.network.registerMessage(SoundUploadedPacket.Handler.class, SoundUploadedPacket.class, 13, Side.SERVER);
	}

	public void init()
	{

	}

	public void UISetup()
	{

	}

	public void soundSetup()
	{

	}

	public void configSetup(File file)
	{

	}

	public ConfigurationManager getConfig()
	{
		return config;
	}
}
