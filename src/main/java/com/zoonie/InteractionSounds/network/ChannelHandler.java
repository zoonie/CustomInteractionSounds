package com.zoonie.InteractionSounds.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.network.message.PlaySoundMessage;
import com.zoonie.InteractionSounds.network.message.RepeatSoundMessage;
import com.zoonie.InteractionSounds.network.message.RequestSoundMessage;
import com.zoonie.InteractionSounds.network.message.ServerSettingsMessage;
import com.zoonie.InteractionSounds.network.message.ServerSoundsMessage;
import com.zoonie.InteractionSounds.network.message.SoundChunkMessage;
import com.zoonie.InteractionSounds.network.message.SoundUploadedMessage;

public class ChannelHandler
{
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(InteractionSounds.MODID);

	public static void init()
	{
		network.registerMessage(RequestSoundMessage.ClientSideHandler.class, RequestSoundMessage.class, 0, Side.CLIENT);
		network.registerMessage(RequestSoundMessage.ServerSideHandler.class, RequestSoundMessage.class, 0, Side.SERVER);

		network.registerMessage(PlaySoundMessage.ClientSideHandler.class, PlaySoundMessage.class, 1, Side.CLIENT);
		network.registerMessage(PlaySoundMessage.ServerSideHandler.class, PlaySoundMessage.class, 1, Side.SERVER);

		network.registerMessage(SoundChunkMessage.Handler.class, SoundChunkMessage.class, 2, Side.CLIENT);
		network.registerMessage(SoundChunkMessage.Handler.class, SoundChunkMessage.class, 2, Side.SERVER);

		network.registerMessage(SoundUploadedMessage.ClientSideHandler.class, SoundUploadedMessage.class, 3, Side.CLIENT);
		network.registerMessage(SoundUploadedMessage.ServerSideHandler.class, SoundUploadedMessage.class, 3, Side.SERVER);

		network.registerMessage(RepeatSoundMessage.ClientSideHandler.class, RepeatSoundMessage.class, 4, Side.CLIENT);
		network.registerMessage(RepeatSoundMessage.ServerSideHandler.class, RepeatSoundMessage.class, 4, Side.SERVER);

		network.registerMessage(ServerSettingsMessage.Handler.class, ServerSettingsMessage.class, 5, Side.CLIENT);

		network.registerMessage(ServerSoundsMessage.Handler.class, ServerSoundsMessage.class, 6, Side.CLIENT);
	}
}
