package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class TickHandler
{
	private int tick;

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(tick == 0 && Minecraft.getMinecraft().gameSettings.keyBindAttack.isKeyDown())
		{
			InteractionHandler.getInstance().detectNewTarget();
		}
		tick = ++tick % 10;
	}
}
