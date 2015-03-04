package com.zoonie.custominteractionsounds.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.zoonie.custominteractionsounds.gui.mapping.GuiInteractionSoundMapping;
import com.zoonie.custominteractionsounds.gui.viewing.GuiListContainer;
import com.zoonie.custominteractionsounds.interaction.InteractionHandler;

import cpw.mods.fml.common.network.IGuiHandler;

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
			return new GuiInteractionSoundMapping(player, InteractionHandler.getInstance().currentInteraction);
		case 1:
			return new GuiListContainer(player);
		default:
			return null;
		}
	}
}
