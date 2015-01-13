package com.zoonie.InteractionSounds.EventHandlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.InteractionSounds.proxy.ClientProxy;

public class InteractionHandler 
{
	@SubscribeEvent
	public void onClick(MouseEvent event)
	{
		if((event.button == 0 || event.button == 1) )
		{
			Interaction interaction = createInteraction(event);
			if(ClientProxy.recordInteraction.isPressed())
			{
				//TODO - pass interaction to gui and perform these:
				//ClientProxy.interactions.remove(interaction);
				//interaction.setSound(soundgatheredfromgui);
				//interaction.setDelay(delaygatheredfromgui);
				//ClientProxy.interactions.add(interaction);
			}
			else if(ClientProxy.interactions.contains(interaction))
			{
				//play assigned sound
			}
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
