package com.zoonie.custominteractionsounds.interaction;

import java.io.File;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.compat.BlockPos;
import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;
import com.zoonie.custominteractionsounds.configuration.MappingsConfigManager;
import com.zoonie.custominteractionsounds.configuration.ServerSettingsConfig;
import com.zoonie.custominteractionsounds.network.ChannelHandler;
import com.zoonie.custominteractionsounds.network.message.PlaySoundMessage;
import com.zoonie.custominteractionsounds.network.message.RequestSoundMessage;
import com.zoonie.custominteractionsounds.sound.DelayedPlayHandler;
import com.zoonie.custominteractionsounds.sound.Sound;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundHelper;
import com.zoonie.custominteractionsounds.sound.SoundInfo;
import com.zoonie.custominteractionsounds.sound.SoundPlayer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

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
	private static InteractionHandler instance = new InteractionHandler();
	public Interaction currentInteraction;
	private BlockPos lastPos;
	private boolean stopSound = false;
	private Minecraft mc = Minecraft.getMinecraft();

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
		if(event.button == 0)
		{
			SoundPlayer.getInstance().stopLeftClickLoop();
			stopSound = false;
		}
		if(event.button == 1)
		{
			SoundPlayer.getInstance().stopRightClickLoop();
		}
		
		// Left or right click and mouse click down.
		if((event.button == 0 || event.button == 1) && event.buttonstate)
		{
			Interaction interaction = createInteraction(event.button);
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

			if(KeyBindings.recordInteraction.getIsKeyPressed())
			{
				currentInteraction = interaction;

				World world = Minecraft.getMinecraft().theWorld;
				player.openGui(CustomInteractionSounds.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				event.setCanceled(true);
			}
			else
				processClick(interaction, player);
		}
		else
		{
			lastPos = null;
		}
	}

	private void processClick(Interaction interaction, EntityPlayerSP player)
	{
		if(MappingsConfigManager.mappings != null)
		{
			String click = interaction.getMouseButton();
			Interaction general = new Interaction(click, interaction.getItem(), interaction.getGeneralTargetName());
			if(MappingsConfigManager.mappings.containsKey(interaction))
			{
				playSound(interaction, player);
			}
			else if(MappingsConfigManager.mappings.containsKey(general))
			{
				playSound(general, player);
			}
			else
			{
				String target = interaction.getTarget();
				String generalTarget = interaction.getGeneralTargetName();
				String item = interaction.getItem();
				String stringAny = "interaction.any";
				String anyBlock = "interaction.any.block";
				String anyEntity = "interaction.any.entity";

				Interaction anyItem = new Interaction(click, stringAny, target);
				Interaction anyItemGeneneralTarget = new Interaction(click, stringAny, generalTarget);
				Interaction anyBlockTarget = new Interaction(click, item, anyBlock);
				Interaction anyEntityTarget = new Interaction(click, item, anyEntity);
				Interaction anyBlockTargetItem = new Interaction(click, stringAny, anyBlock);
				Interaction anyEntityTargetItem = new Interaction(click, stringAny, anyEntity);
				Interaction anyTarget = new Interaction(click, item, stringAny);
				Interaction any = new Interaction(click, stringAny, stringAny);

				if(MappingsConfigManager.mappings.containsKey(anyItem))
					playSound(anyItem, player);
				else if(MappingsConfigManager.mappings.containsKey(anyItemGeneneralTarget))
					playSound(anyItemGeneneralTarget, player);
				else if(MappingsConfigManager.mappings.containsKey(anyBlockTarget) && !interaction.isEntity())
					playSound(anyBlockTarget, player);
				else if(MappingsConfigManager.mappings.containsKey(anyEntityTarget) && interaction.isEntity())
					playSound(anyEntityTarget, player);
				else if(MappingsConfigManager.mappings.containsKey(anyBlockTargetItem) && !interaction.isEntity())
					playSound(anyBlockTargetItem, player);
				else if(MappingsConfigManager.mappings.containsKey(anyEntityTargetItem) && interaction.isEntity())
					playSound(anyEntityTargetItem, player);
				else if(MappingsConfigManager.mappings.containsKey(anyTarget))
					playSound(anyTarget, player);
				else if(MappingsConfigManager.mappings.containsKey(any))
					playSound(any, player);
			}
		}
	}

	private void playSound(Interaction interaction, EntityPlayerSP player)
	{
		if(!interaction.isEntity())
			stopSound = true;

		BlockPos pos = getTargetPos();

		Sound sound = MappingsConfigManager.mappings.get(interaction);
		String soundName = sound.getSoundName();
		String category = sound.getCategory();
		File soundLocation = sound.getSoundLocation();
		float volume = sound.getVolume();

		if(!soundLocation.exists())
		{
			String loop = null;
			if(interaction.getMouseButton().equals("left") && !interaction.isEntity() && !interaction.getTarget().equals("tile.air"))
				loop = "left";
			else if(interaction.getMouseButton().equals("right"))
				loop = "right";

			SoundInfo soundInfo = new SoundInfo(soundName, category);
			SoundHandler.addRemoteSound(soundInfo);
			DelayedPlayHandler.addDelayedPlay(soundInfo, UUID.randomUUID().toString(), pos, volume, loop);
			ChannelHandler.network.sendToServer(new RequestSoundMessage(soundInfo.name, soundInfo.category));
		}
		else
		{
			String identifier = player.getDisplayName() + soundName + category;
			if(!SoundPlayer.getInstance().isPlaying(identifier))
				SoundPlayer.getInstance().playNewSound(soundLocation, identifier, pos, true, volume);
			Double soundLength = SoundHelper.getSoundLength(soundLocation);

			if(interaction.getMouseButton().equals("left") && !interaction.isEntity() && !interaction.getTarget().equals("tile.air"))
				SoundPlayer.getInstance().setLeftClickLoop(identifier, soundLength, pos);
			else if(interaction.getMouseButton().equals("right"))
			{
				SoundPlayer.getInstance().setRightClickLoop(identifier, soundLength, pos);
			}

			if(SoundHelper.getSoundLength(soundLocation) <= ServerSettingsConfig.MaxSoundLength)
			{
				ChannelHandler.network.sendToServer(new RequestSoundMessage(soundName, category, true));
				ChannelHandler.network.sendToServer(new PlaySoundMessage(soundName, category, identifier, player.dimension, pos, volume, player.getDisplayName()));
			}
		}
	}

	public void detectNewTarget(String click)
	{
		if(this.lastPos != null && !getTargetPos().equals(this.lastPos))
		{
			if(click.equals("left"))
			{
				detectNewLeftTarget();
			}
			else if(click.equals("right"))
			{
				detectNewRightTarget();
			}
			else if(click.equals("both"))
			{
				detectNewLeftTarget();
				detectNewRightTarget();
			}
		}
		lastPos = getTargetPos();
	}

	private void detectNewLeftTarget()
	{
		Interaction interaction = createInteraction(0);
		SoundPlayer.getInstance().stopLeftClickLoop();
		if(!interaction.isEntity() && !interaction.getTarget().equals("tile.air"))
		{
			processClick(interaction, Minecraft.getMinecraft().thePlayer);
		}
	}

	private void detectNewRightTarget()
	{
		Interaction interaction = createInteraction(1);

		if(!Minecraft.getMinecraft().theWorld.isAirBlock(lastPos.getX(), lastPos.getY(), lastPos.getZ()))
		{
			SoundPlayer.getInstance().stopRightClickLoop();
			processClick(interaction, Minecraft.getMinecraft().thePlayer);
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
		EntityPlayerSP player = mc.thePlayer;

		String item = "item.hand";
		if(player.getCurrentEquippedItem() != null)
			item = player.getCurrentEquippedItem().getUnlocalizedName();

		String click = button == 0 ? "left" : "right";

		MovingObjectPosition mop = mc.objectMouseOver;

		if(mop.typeOfHit == MovingObjectType.ENTITY)
		{
			Entity entity = mop.entityHit;
			String generalCategory;
			String className = entity.getClass().getName();
			if(className.contains("passive"))
				generalCategory = "entity.passive";
			else if(className.contains("monster"))
				generalCategory = "entity.monsters";
			else if(className.contains("boss"))
				generalCategory = "entity.bosses";
			else if(className.contains("player"))
				generalCategory = "entity.players";
			else
				generalCategory = entity.getCommandSenderName();

			if(EntityList.getEntityString(entity) == null)
			{
				Interaction in = new Interaction(click, item, entity.getCommandSenderName(), generalCategory);
				in.setIsEntity(true);
				return in;
			}
			else
			{
				Interaction in = new Interaction(click, item, "entity." + EntityList.getEntityString(entity), generalCategory);
				in.setIsEntity(true);
				return in;
			}
		}
		else
		{
			Block b = mc.theWorld.getBlock(mop.blockX, mop.blockY, mop.blockZ);
			String name = getUnlocalizedName(b);
			String generalName = getUnlocalizedParentName(name);

			return new Interaction(click, item, name, generalName);
		}
	}

	private String getUnlocalizedName(Block b)
	{
		ItemStack is = new ItemStack(b, 1);
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

	@SubscribeEvent
	public void stopSound(PlaySoundEvent event)
	{
		if(ClientConfigHandler.soundOverride && stopSound && lastPos != null)
		{
			double soundX = Math.floor(event.x);
			double soundY = Math.floor(event.y);
			double soundZ = Math.floor(event.z);

			BlockPos pos = lastPos;
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			if(soundX == x && soundY == y && soundZ == z && !event.name.contains(":dig."))
			{
				event.result = null;
			}
		}
	}

	private BlockPos getTargetPos()
	{
		MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
		if(mop.typeOfHit == MovingObjectType.ENTITY)
		{
			Entity entity = mop.entityHit;
			return new BlockPos((int) entity.posX, (int) entity.posY, (int) entity.posZ);
		}
		else
			return new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
	}

	public static InteractionHandler getInstance()
	{
		return instance;
	}
}
