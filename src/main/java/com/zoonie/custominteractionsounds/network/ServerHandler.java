package com.zoonie.custominteractionsounds.network;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.network.message.ServerSettingsMessage;
import com.zoonie.custominteractionsounds.network.message.ServerSoundsMessage;
import com.zoonie.custominteractionsounds.sound.Sound;
import com.zoonie.custominteractionsounds.sound.SoundHandler;

public class ServerHandler
{
	@SubscribeEvent
	public void onPlayerJoin(EntityJoinWorldEvent event)
	{
		if(event.entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) event.entity;
			ChannelHandler.network.sendTo(new ServerSettingsMessage(), player);
			if(!ServerSettingsConfig.UseServerMappings)
			{
				SoundHandler.reloadSounds();
				ChannelHandler.network.sendTo(new ServerSoundsMessage(player, new ArrayList<Sound>(SoundHandler.getSounds().values())), player);
			}
		}
	}
}
