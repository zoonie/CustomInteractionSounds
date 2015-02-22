package com.zoonie.interactionsounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.zoonie.interactionsounds.sound.SoundPlayer;

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
		if(keyBindAttack.isKeyDown() && keyBindUseItem.isKeyDown())
		{
			soundPlayer.updateLeftClickLoop();
			soundPlayer.updateRightClickLoop();
			interactionHandler.detectNewTarget("both");
		}
		else if(keyBindAttack.isKeyDown())
		{
			soundPlayer.updateLeftClickLoop();
			interactionHandler.detectNewTarget("left");
		}
		else if(keyBindUseItem.isKeyDown())
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
