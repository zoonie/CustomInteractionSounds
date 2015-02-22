package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class TickHandler
{
	private KeyBinding keyBindAttack = Minecraft.getMinecraft().gameSettings.keyBindAttack;
	private KeyBinding keyBindUseItem = Minecraft.getMinecraft().gameSettings.keyBindUseItem;
	private SoundPlayer soundPlayer = SoundPlayer.getInstance();
	private InteractionHandler interactionHandler = InteractionHandler.getInstance();
	private int tick = 0;

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

		if(tick == 0)
			SoundPlayer.getInstance().cleanUp();

		tick = ++tick % 100;
	}
}
