package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class TickHandler
{
	@SubscribeEvent
	public void onPlayerTick(ClientTickEvent event)
	{
		if(Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown())
		{
			SoundPlayer.getInstance().checkLooping();
			InteractionHandler.getInstance().detectNewTarget();
		}
	}
}
