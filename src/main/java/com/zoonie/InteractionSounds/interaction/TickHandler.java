package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class TickHandler
{
	private int tick;

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(tick == 0 && Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown())
		{
			SoundPlayer.getInstance().checkLooping();
			InteractionHandler.getInstance().detectNewTarget();
		}
		tick = ++tick % 10;
	}
}
