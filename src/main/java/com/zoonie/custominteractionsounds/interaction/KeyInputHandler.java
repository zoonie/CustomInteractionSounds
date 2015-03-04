package com.zoonie.custominteractionsounds.interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyInputHandler
{
	@SubscribeEvent
	public void onKeyPress(KeyInputEvent event)
	{
		if(KeyBindings.listSavedInteractions.isPressed())
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			World world = Minecraft.getMinecraft().theWorld;
			player.openGui(CustomInteractionSounds.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
}
