package com.zoonie.InteractionSounds.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.InteractionSounds.network.message.ServerSettingsMessage;

public class ServerHandler
{
	@SubscribeEvent
	public void connect(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityPlayerMP)
			ChannelHandler.network.sendTo(new ServerSettingsMessage(), (EntityPlayerMP) event.entity);
	}
}
