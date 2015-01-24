package com.zoonie.InteractionSounds.handler.event;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.handler.ChannelHandler;
import com.zoonie.InteractionSounds.network.packet.client.ClientPlaySoundMessage;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;

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
	private boolean buttonState = false;

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
			buttonState = true;
			Interaction interaction = createInteraction(event.button);
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

			if(ClientProxy.recordInteraction.isPressed())
			{
				reopenGui = true;
				currentInteraction = interaction;

				World world = Minecraft.getMinecraft().theWorld;
				player.openGui(InteractionSounds.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				event.setCanceled(true);
			}
			else
				processClick(interaction, event.button, player);
		}
		else if((event.button == 0 || event.button == 1))
			buttonState = false;
	}

	private void processClick(Interaction interaction, int button, EntityPlayerSP player)
	{
		if(ClientProxy.mappings != null)
		{
			String click = button == 0 ? "left" : "right";
			if(lookUp(interaction, click, player, false))
				return;
			lookUp(interaction, click, player, true);
		}
	}

	private Boolean lookUp(Interaction interaction, String click, EntityPlayerSP player, Boolean useGeneral)
	{
		if(useGeneral)
			interaction.useGeneralTargetName();
		if(ClientProxy.mappings.containsKey(interaction))
			return playSound(interaction, player);
		else
		{
			Interaction anyItem = new Interaction(click, "any", interaction.getTarget(), interaction.getGeneralTargetName());
			Interaction anyTarget = new Interaction(click, interaction.getItem(), "any", interaction.getGeneralTargetName());
			Interaction any = new Interaction(click, "any", "any", interaction.getGeneralTargetName());

			if(ClientProxy.mappings.containsKey(anyItem))
				return playSound(anyItem, player);
			else if(ClientProxy.mappings.containsKey(anyTarget))
				return playSound(anyTarget, player);
			else if(ClientProxy.mappings.containsKey(any))
				return playSound(any, player);
		}
		return false;
	}

	private Boolean playSound(Interaction interaction, EntityPlayerSP player)
	{
		Sound sound = ClientProxy.mappings.get(interaction);
		String id = UUID.randomUUID().toString();
		ChannelHandler.network.sendToServer(new ClientPlaySoundMessage(sound.getSoundName(), id, player.dimension, (int) player.posX, (int) player.posY,
				(int) player.posZ));
		return true;
	}

	@SubscribeEvent
	public void onBreak(BreakEvent event)
	{
		if(buttonState)
		{
			Interaction interaction = createInteraction(0);
			processClick(interaction, 0, Minecraft.getMinecraft().thePlayer);
		}
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
	private Interaction createInteraction(int button)
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
			Block b = bs.getBlock();
			String name = getUnlocalizedName(b, bs);
			String generalName;
			String unlocalizedParentName = getUnlocalizedParentName(name);

			if(StatCollector.canTranslate(unlocalizedParentName + ".name"))
				generalName = unlocalizedParentName;
			else
				generalName = getUnlocalizedName(b, b.getDefaultState());

			return new Interaction(button == 0 ? "left" : "right", item, name, generalName);
		}
		else if(entity != null)
		{
			if(EntityList.getEntityString(entity) == null || entity.hasCustomName())
				return new Interaction(button == 0 ? "left" : "right", item, entity.getName());
			else
				return new Interaction(button == 0 ? "left" : "right", item, "entity." + EntityList.getEntityString(entity));
		}
		else
			return null;
	}

	private String getUnlocalizedName(Block b, IBlockState bs)
	{
		ItemStack is = new ItemStack(b, 1, b.getMetaFromState(bs));
		if(is.getItem() != null)
			return is.getUnlocalizedName();
		return b.getUnlocalizedName();
	}

	private String getUnlocalizedParentName(String child)
	{
		String[] array = child.split("\\.");
		if(array.length == 3)
			return array[0] + "." + array[1];
		return child;
	}
}
