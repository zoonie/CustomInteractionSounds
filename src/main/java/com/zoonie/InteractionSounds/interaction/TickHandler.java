package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class TickHandler
{
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		GameSettings gameSettings = Minecraft.getMinecraft().gameSettings;
		if(gameSettings.keyBindAttack.isKeyDown() && gameSettings.keyBindUseItem.isKeyDown())
		{
			SoundPlayer.getInstance().updateLeftClickLoop();
			SoundPlayer.getInstance().updateRightClickLoop();
			InteractionHandler.getInstance().detectNewTarget("both");
		}
		else if(gameSettings.keyBindAttack.isKeyDown())
		{
			SoundPlayer.getInstance().updateLeftClickLoop();
			InteractionHandler.getInstance().detectNewTarget("left");
		}
		else if(gameSettings.keyBindUseItem.isKeyDown())
		{
			SoundPlayer.getInstance().updateRightClickLoop();
			InteractionHandler.getInstance().detectNewTarget("right");
		}
		SoundPlayer.getInstance().cleanUp();
	}
}
