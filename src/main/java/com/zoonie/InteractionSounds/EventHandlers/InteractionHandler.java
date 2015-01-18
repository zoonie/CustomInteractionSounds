package com.zoonie.InteractionSounds.EventHandlers;

import java.util.UUID;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
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
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

/**
 * The InteractionHandler class deals with specific Forge events, mainly
 * MouseEvents, to gather data to aid in the creation of Interaction objects.
 * The initial interaction data such as mouse click, item in use and
 * block/entity in focus is gathered here. If the player is trying to record an
 * interaction, the sounds GUI will be opened which will be used to assign a
 * sound to the interaction. This interaction will be stored globally in a list.
 * When a player performs an interaction and the interaction is in the list,
 * then the assigned sound will be played.
 * 
 * @author zoonie
 *
 */
public class InteractionHandler
{
	public static Interaction currentInteraction;
	private boolean reopenGui = false;

	/**
	 * Called when user clicks with mouse. If the record interaction key has
	 * been pressed, the GUI for selecting a sound will be opened which can set
	 * sound of interaction and add it to global list of interactions.
	 * Otherwise, it will check if the current mouse left/right click
	 * interaction is in the list of interactions and play the assigned sound if
	 * it is.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void onClick(MouseEvent event)
	{
		reopenGui = false;

		// Left or right click and mouse click down.
		if((event.button == 0 || event.button == 1) && event.buttonstate)
		{
			Interaction interaction = createInteraction(event);
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

			if(ClientProxy.recordInteraction.isPressed())
			{
				reopenGui = true;
				currentInteraction = interaction;

				World world = Minecraft.getMinecraft().theWorld;
				player.openGui(InteractionSounds.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				event.setCanceled(true);
			}
			else if(ClientProxy.mappings != null)
			{
				if(ClientProxy.mappings.containsKey(interaction))
				{
					playSound(interaction, player);
				}
				else if(event.button == 0)
				{
					Interaction leftAnyItem = new Interaction("left", "any", interaction.getTarget());
					Interaction leftAnyTarget = new Interaction("left", interaction.getItem(), "any");
					Interaction leftAny = new Interaction("left", "any", "any");
					if(ClientProxy.mappings.containsKey(leftAnyItem))
						playSound(leftAnyItem, player);
					else if(ClientProxy.mappings.containsKey(leftAnyTarget))
						playSound(leftAnyTarget, player);
					else if(ClientProxy.mappings.containsKey(leftAny))
						playSound(leftAny, player);
				}
				else if(event.button == 1)
				{
					Interaction rightAnyItem = new Interaction("right", "any", interaction.getTarget());
					Interaction rightAnyTarget = new Interaction("right", interaction.getItem(), "any");
					Interaction rightAny = new Interaction("right", "any", "any");
					if(ClientProxy.mappings.containsKey(rightAnyItem))
						playSound(rightAnyItem, player);
					else if(ClientProxy.mappings.containsKey(rightAnyTarget))
						playSound(rightAnyTarget, player);
					else if(ClientProxy.mappings.containsKey(rightAny))
						playSound(rightAny, player);
				}
			}
		}
	}

	private void playSound(Interaction interaction, EntityPlayerSP player)
	{
		Sound sound = ClientProxy.mappings.get(interaction);
		String id = UUID.randomUUID().toString();
		SoundPlayer.playSound(sound.getSoundLocation(), id, (float) player.posX, (float) player.posY, (float) player.posZ, true);
		sound.setTimeLastPlayed();
		ClientProxy.mappings.put(interaction, sound);
	}

	/**
	 * Reopens sounds GUI if the GuiScreenEvent is from a different GUI while
	 * trying to open the sounds GUI.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void guiScreen(GuiScreenEvent event)
	{
		if(event.gui != null && reopenGui && !event.gui.getClass().getSimpleName().equals("GuiSounds"))
		{
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			World world = Minecraft.getMinecraft().theWorld;
			player.openGui(InteractionSounds.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}

	/**
	 * Clears unwanted GUIs from screen.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public void guiOpen(GuiOpenEvent event)
	{
		if(event.gui != null && reopenGui && !event.gui.getClass().getSimpleName().equals("GuiSounds"))
		{
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			player.closeScreen();
		}
	}

	/**
	 * Constructs an Interaction object using data from {@code event}.
	 * 
	 * @param event
	 * @return - Returns an Interaction object with data on mouse button, item
	 *         in use and block/entity that the player is looking at.
	 */
	private Interaction createInteraction(MouseEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		String item = "Hand";
		if(player.getCurrentEquippedItem() != null)
			item = player.getCurrentEquippedItem().getDisplayName();
		MovingObjectPosition mop = mc.objectMouseOver;
		BlockPos pos = mop.getBlockPos();
		Entity entity = mop.entityHit;
		if(pos != null)
		{
			IBlockState bs = mc.theWorld.getBlockState(pos);
			return new Interaction(event.button == 0 ? "left" : "right", item, bs.getBlock().getLocalizedName());
		}
		else if(entity != null)
			return new Interaction(event.button == 0 ? "left" : "right", item, entity.getName());
		else
			return null;
	}

	private String getBlockVariant(IBlockState bs)
	{
		for(int i = 0; i < bs.getPropertyNames().toArray().length; i++)
		{
			Object o = bs.getPropertyNames().toArray()[i];
			Object o0 = bs.getProperties().get(o);
			IProperty p = (IProperty) o;
			if(p.getName().equals("variant"))
				return bs.getValue(p).toString();
		}
		return null;
	}
}
