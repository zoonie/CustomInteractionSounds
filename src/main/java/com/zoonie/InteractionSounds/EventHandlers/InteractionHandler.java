package com.zoonie.InteractionSounds.EventHandlers;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
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
					return;
				}

				String click = event.button == 0 ? "left" : "right";

				Interaction anyItem = new Interaction(click, "any", interaction.getTarget());
				Interaction anyTarget = new Interaction(click, interaction.getItem(), "any");
				Interaction any = new Interaction(click, "any", "any");

				if(ClientProxy.mappings.containsKey(anyItem))
					playSound(anyItem, player);
				else if(ClientProxy.mappings.containsKey(anyTarget))
					playSound(anyTarget, player);
				else if(ClientProxy.mappings.containsKey(any))
					playSound(any, player);
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
		String item = "hand";
		if(player.getCurrentEquippedItem() != null)
			item = player.getCurrentEquippedItem().getUnlocalizedName();
		MovingObjectPosition mop = mc.objectMouseOver;
		BlockPos pos = mop.getBlockPos();
		Entity entity = mop.entityHit;
		if(pos != null)
		{
			IBlockState bs = mc.theWorld.getBlockState(pos);
			String name = bs.getBlock().getUnlocalizedName();
			String variant = getBlockVariant(bs, pos);
			return new Interaction(event.button == 0 ? "left" : "right", item, name, variant);
		}
		else if(entity != null)
		{
			return new Interaction(event.button == 0 ? "left" : "right", item, "entity." + EntityList.getEntityString(entity));
		}
		else
			return null;
	}

	/**
	 * Method to get unlocalized name of relevant property that identifies the
	 * variant of block. Note: This method will be replaced when alternative
	 * approach discovered.
	 * 
	 * @param bs
	 * @param pos
	 * @return Unlocalized name for the relevant property of the block.
	 */
	private String getBlockVariant(IBlockState bs, BlockPos pos)
	{
		String variant = "";
		for(int i = 0; i < bs.getPropertyNames().toArray().length; i++)
		{
			IProperty p = (IProperty) bs.getPropertyNames().toArray()[i];
			if(p.getName().equals("variant"))
				return "." + getUnlocalized(bs, p);
			else if(p.getName().equals("color"))
				return "." + getUnlocalized(bs, p);
			else if(p.getName().equals("type"))
				return "." + getUnlocalized(bs, p);
			else if(p.getName().equals("damage"))
			{
				if(Integer.parseInt(getUnlocalized(bs, p)) == 0)
					return variant;
				if(Integer.parseInt(getUnlocalized(bs, p)) == 1)
					return "." + "slightlyDamaged";
				if(Integer.parseInt(getUnlocalized(bs, p)) == 2)
					return "." + "veryDamaged";
			}
			else if(p.getName().equals("wet"))
				return "." + (((Boolean) (bs.getValue(p))) == true ? "wet" : "dry");
		}
		return variant;
	}

	/**
	 * Reflection to get unlocalized name of block property.
	 * 
	 * @param bs
	 * @param p
	 * @return Unlocalized string
	 */
	private String getUnlocalized(IBlockState bs, IProperty p)
	{
		try
		{
			return (String) bs.getValue(p).getClass().getMethod("getUnlocalizedName").invoke(bs.getValue(p));
		} catch(IllegalAccessException e)
		{
			e.printStackTrace();
		} catch(NoSuchMethodException e)
		{
			return bs.getValue(p).toString();
		} catch(InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
