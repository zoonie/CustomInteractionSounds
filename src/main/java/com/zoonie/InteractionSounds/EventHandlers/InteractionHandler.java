package com.zoonie.InteractionSounds.EventHandlers;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class InteractionHandler 
{
	public static Interaction currentInteraction;
	private boolean reOpenGui = false;
	
	@SubscribeEvent
	public void onClick(MouseEvent event)
	{
		reOpenGui = false;
		if((event.button == 0 || event.button == 1) && event.buttonstate )
		{
			Interaction interaction = createInteraction(event);
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			if(ClientProxy.recordInteraction.isPressed())
			{
				reOpenGui=true;
				currentInteraction = interaction;
				World world = Minecraft.getMinecraft().theWorld;
				player.openGui(InteractionSounds.instance, 0, world, (int)player.posX, (int)player.posY, (int)player.posZ);
			}
			else if(ClientProxy.interactions != null && ClientProxy.interactions.contains(interaction))
			{
				int index = ClientProxy.interactions.indexOf(interaction);
				Interaction inter  = ClientProxy.interactions.get(index);
				if(System.currentTimeMillis() - inter.getTimeLastPlayed() > SoundHelper.getSoundLength(inter.getSound().getSoundLocation())*1000)
				{
					String id = UUID.randomUUID().toString();
					SoundPlayer.playSound(inter.getSound().getSoundLocation(), id, (float)player.posX, (float)player.posY, (float)player.posZ, true);
					inter.setTimeLastPlayed();
				}
			}
		}
	}
	
	@SubscribeEvent
	public void guiScreen(GuiScreenEvent event)
	{
		if(event.gui!=null && reOpenGui && !event.gui.getClass().getSimpleName().equals("GuiSounds"))
		{
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			World world = Minecraft.getMinecraft().theWorld;
			player.openGui(InteractionSounds.instance, 0, world, (int)player.posX, (int)player.posY, (int)player.posZ);	
		}
	}
	
	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event)
	{
		if(event.gui!=null && reOpenGui && !event.gui.getClass().getSimpleName().equals("GuiSounds"))
		{
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			player.closeScreen();
		}
	}
	private Interaction createInteraction(MouseEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		String item = "Hand";
		if(player.getCurrentEquippedItem() != null)
			item = player.getCurrentEquippedItem().getUnlocalizedName();
		MovingObjectPosition mop = mc.objectMouseOver;
		BlockPos pos = mop.getBlockPos();
		Entity entity = mop.entityHit;
		
		if(pos != null)
			return new Interaction(event.button, item, mc.theWorld.getBlockState(pos).getBlock().getUnlocalizedName());
		else if(entity != null)
			return new Interaction(event.button, item, entity.getName());
		else
			return null;
	}
}
