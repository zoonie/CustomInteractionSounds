package com.zoonie.custominteractionsounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import com.zoonie.custominteractionsounds.sound.SoundPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class TickHandler
{
	private Minecraft minecraft = Minecraft.getMinecraft();
	private KeyBinding keyBindAttack = minecraft.gameSettings.keyBindAttack;
	private KeyBinding keyBindUseItem = minecraft.gameSettings.keyBindUseItem;
	private SoundPlayer soundPlayer = SoundPlayer.getInstance();
	private InteractionHandler interactionHandler = InteractionHandler.getInstance();
	private int tick = 0;
	private boolean paused;

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if(keyBindAttack.getIsKeyPressed() && keyBindUseItem.getIsKeyPressed())
		{
			soundPlayer.updateLeftClickLoop();
			soundPlayer.updateRightClickLoop();
			interactionHandler.detectNewTarget("both");
		}
		else if(keyBindAttack.getIsKeyPressed())
		{
			soundPlayer.updateLeftClickLoop();
			interactionHandler.detectNewTarget("left");
		}
		else if(keyBindUseItem.getIsKeyPressed())
		{
			soundPlayer.updateRightClickLoop();
			interactionHandler.detectNewTarget("right");
		}

		if(paused == false && minecraft.isGamePaused())
		{
			SoundPlayer.getInstance().pauseSounds();
			paused = true;
		}
		else if(paused == true && !minecraft.isGamePaused())
		{
			SoundPlayer.getInstance().resumeSounds();
			paused = false;
		}

		if(tick == 0)
		{
			SoundPlayer.getInstance().cleanUp();
		}

		tick = ++tick % 100;
	}
}
