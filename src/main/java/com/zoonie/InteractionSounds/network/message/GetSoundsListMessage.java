package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;

public class GetSoundsListMessage implements IMessage
{
	public GetSoundsListMessage()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
	}

	public static class Handler implements IMessageHandler<GetSoundsListMessage, IMessage>
	{
		@Override
		public IMessage onMessage(GetSoundsListMessage message, MessageContext ctx)
		{
			if(!MinecraftServer.getServer().isDedicatedServer())
				return null;

			SoundHandler.reloadSounds();

			EntityPlayerMP player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
			ChannelHandler.network.sendTo(new ServerSoundsMessage(player, new ArrayList<Sound>(SoundHandler.getSounds().values())), player);
			return null;
		}
	}
}
