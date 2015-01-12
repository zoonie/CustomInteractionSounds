package com.zoonie.InteractionSounds.EventHandlers;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InteractionHandler 
{
	@SubscribeEvent
	public void onInteractionWithBlock(PlayerInteractEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote)
		{
			World world = event.world;
			Block block = world.getBlockState(event.pos).getBlock();
		}
	}
	
	@SubscribeEvent
	public void onRightClickEntity(EntityInteractEvent event)
	{
		Entity target = event.target;
		ItemStack item = event.entityPlayer.getCurrentEquippedItem();
	}
	
	@SubscribeEvent
	public void onLeftClickEntity(AttackEntityEvent event)
	{
		if(!event.entityPlayer.worldObj.isRemote)
		{
			Entity target = event.target;
			ItemStack item = event.entityPlayer.getCurrentEquippedItem();
		}
	}
}
