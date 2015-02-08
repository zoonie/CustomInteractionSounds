package com.zoonie.InteractionSounds.handler;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.network.packet.SoundChunkPacket;
import com.zoonie.InteractionSounds.network.packet.SoundUploadedPacket;
import com.zoonie.InteractionSounds.network.packet.client.PlaySoundMessage;
import com.zoonie.InteractionSounds.network.packet.client.RequestSoundMessage;
import com.zoonie.InteractionSounds.network.packet.server.SoundNotFoundPacket;
import com.zoonie.InteractionSounds.network.packet.server.StopSoundPacket;

public class ChannelHandler
{
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(InteractionSounds.MODID);

	public static void init()
	{
		network.registerMessage(RequestSoundMessage.ServerSideHandler.class, RequestSoundMessage.class, 0, Side.SERVER);
		network.registerMessage(RequestSoundMessage.ClientSideHandler.class, RequestSoundMessage.class, 0, Side.CLIENT);
		network.registerMessage(PlaySoundMessage.ServerSideHandler.class, PlaySoundMessage.class, 3, Side.SERVER);

		network.registerMessage(PlaySoundMessage.ClientSideHandler.class, PlaySoundMessage.class, 6, Side.CLIENT);
		network.registerMessage(SoundNotFoundPacket.Handler.class, SoundNotFoundPacket.class, 7, Side.CLIENT);
		network.registerMessage(StopSoundPacket.Handler.class, StopSoundPacket.class, 10, Side.CLIENT);

		network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.CLIENT);
		network.registerMessage(SoundChunkPacket.Handler.class, SoundChunkPacket.class, 12, Side.SERVER);

		network.registerMessage(SoundUploadedPacket.ClientSideHandler.class, SoundUploadedPacket.class, 13, Side.CLIENT);
		network.registerMessage(SoundUploadedPacket.ServerSideHandler.class, SoundUploadedPacket.class, 13, Side.SERVER);
	}
}
