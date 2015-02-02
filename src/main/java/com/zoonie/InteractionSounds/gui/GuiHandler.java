package com.zoonie.InteractionSounds.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.zoonie.InteractionSounds.gui.mapping.GuiInteractionSoundMapping;
import com.zoonie.InteractionSounds.gui.viewing.GuiListContainer;
import com.zoonie.InteractionSounds.handler.event.InteractionHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
		default:
			return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch(ID)
		{
		case 0:
			return new GuiInteractionSoundMapping(player, InteractionHandler.currentInteraction);
		case 1:
			return new GuiListContainer(player);
		default:
			return null;
		}
	}
}
