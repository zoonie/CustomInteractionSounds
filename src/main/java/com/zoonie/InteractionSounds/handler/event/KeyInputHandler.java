package com.zoonie.InteractionSounds.handler.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

import com.zoonie.InteractionSounds.InteractionSounds;

public class KeyInputHandler
{
	@SubscribeEvent
	public void onKeyPress(KeyInputEvent event)
	{
		if(KeyBindings.listSavedInteractions.isPressed())
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			World world = Minecraft.getMinecraft().theWorld;
			player.openGui(InteractionSounds.instance, 1, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}
}
