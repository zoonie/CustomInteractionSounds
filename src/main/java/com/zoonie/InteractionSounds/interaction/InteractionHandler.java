package com.zoonie.InteractionSounds.interaction;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.configuration.ClientConfigHandler;
import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.configuration.ServerSettingsConfig;
import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.network.message.PlaySoundMessage;
import com.zoonie.InteractionSounds.network.message.RequestSoundMessage;
import com.zoonie.InteractionSounds.sound.DelayedPlayHandler;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundInfo;
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
	private static InteractionHandler instance = new InteractionHandler();
	public Interaction currentInteraction;
	private BlockPos lastPos;
	private boolean stopSound = false;
	private boolean paused = false;

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
		// Left or right click and mouse click down.
		if((event.button == 0 || event.button == 1) && event.buttonstate)
		{
			Interaction interaction = createInteraction(event.button);
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

			if(KeyBindings.recordInteraction.isKeyDown())
			{
				currentInteraction = interaction;

				World world = Minecraft.getMinecraft().theWorld;
				player.openGui(InteractionSounds.instance, 0, world, (int) player.posX, (int) player.posY, (int) player.posZ);
				event.setCanceled(true);
			}
			else
				processClick(interaction, event.button, player);
		}
		else if(event.button == 0 || event.button == 1)
		{
			SoundPlayer.getInstance().stopAllLooping();
			stopSound = false;
		}
	}

	private void processClick(Interaction interaction, int button, EntityPlayerSP player)
	{
		if(MappingsConfigManager.mappings != null)
		{
			String click = button == 0 ? "left" : "right";
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
		player.swingItem();

		if(!interaction.isEntity())
			stopSound = true;

		BlockPos pos = getTargetPos();
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		Sound sound = MappingsConfigManager.mappings.get(interaction);
		String soundName = sound.getSoundName();
		String category = sound.getCategory();
		File soundLocation = sound.getSoundLocation();
		float volume = sound.getVolume();

		if(!soundLocation.exists())
		{
			boolean loop = false;
			if(interaction.getMouseButton().equals("left") && !interaction.isEntity() && !interaction.getTarget().equals("tile.air"))
				loop = true;

			SoundInfo soundInfo = new SoundInfo(soundName, category);
			SoundHandler.addRemoteSound(soundInfo);
			DelayedPlayHandler.addDelayedPlay(soundInfo, UUID.randomUUID().toString(), x, y, z, volume, loop);
			ChannelHandler.network.sendToServer(new RequestSoundMessage(soundInfo.name, soundInfo.category));
		}
		else
		{
			String identifier = SoundPlayer.getInstance().playNewSound(soundLocation, null, (float) x, (float) y, (float) z, true, volume);
			Double soundLength = (double) TimeUnit.SECONDS.toMillis((long) SoundHelper.getSoundLength(soundLocation));

			if(interaction.getMouseButton().equals("left") && !interaction.isEntity() && !interaction.getTarget().equals("tile.air"))
				SoundPlayer.getInstance().addLoop(identifier, soundLength);

			if(SoundHelper.getSoundLength(soundLocation) <= ServerSettingsConfig.MaxSoundLength)
			{
				ChannelHandler.network.sendToServer(new RequestSoundMessage(soundName, category, true));
				ChannelHandler.network.sendToServer(new PlaySoundMessage(soundName, category, identifier, player.dimension, x, y, z, volume, player.getDisplayNameString()));
			}
		}
	}

	public void detectNewTarget()
	{
		BlockPos lastPos = this.lastPos;
		Interaction interaction = createInteraction(0);

		if(!interaction.isEntity() && !getTargetPos().equals(lastPos) && !interaction.getTarget().equals("tile.air"))
		{
			SoundPlayer.getInstance().stopAllLooping();
			processClick(interaction, 0, Minecraft.getMinecraft().thePlayer);
		}
		else if(interaction.isEntity() || interaction.getTarget().equals("tile.air"))
			SoundPlayer.getInstance().stopAllLooping();
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

		String item = "item.hand";
		if(player.getCurrentEquippedItem() != null)
			item = player.getCurrentEquippedItem().getUnlocalizedName();

		MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
		lastPos = mop.getBlockPos();
		Entity entity = mop.entityHit;

		if(lastPos != null)
		{
			IBlockState bs = mc.theWorld.getBlockState(lastPos);
			Block b = bs.getBlock();
			String name = getUnlocalizedName(b, bs);
			String generalName = getUnlocalizedParentName(name);

			return new Interaction(button == 0 ? "left" : "right", item, name, generalName);
		}
		else if(entity != null)
		{
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
				generalCategory = entity.getName();

			if(EntityList.getEntityString(entity) == null || entity.hasCustomName())
			{
				Interaction in = new Interaction(button == 0 ? "left" : "right", item, entity.getName(), generalCategory);
				in.setIsEntity(true);
				return in;
			}
			else
			{
				Interaction in = new Interaction(button == 0 ? "left" : "right", item, "entity." + EntityList.getEntityString(entity), generalCategory);
				in.setIsEntity(true);
				return in;
			}
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

	@SubscribeEvent
	public void stopSound(PlaySoundEvent event)
	{
		if(ClientConfigHandler.soundOverride && stopSound)
		{
			ISound sound = event.sound;
			String soundID = sound.getSoundLocation().toString();
			double soundX = Math.floor(sound.getXPosF());
			double soundY = Math.floor(sound.getYPosF());
			double soundZ = Math.floor(sound.getZPosF());

			BlockPos pos = getTargetPos();
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();

			if(soundX == x && soundY == y && soundZ == z && !soundID.contains(":dig."))
			{
				event.result = null;
			}
		}
	}

	private BlockPos getTargetPos()
	{
		MovingObjectPosition mop = Minecraft.getMinecraft().objectMouseOver;
		if(mop.getBlockPos() != null)
			return mop.getBlockPos();
		else
			return mop.entityHit.getPosition();
	}

	public static InteractionHandler getInstance()
	{
		return instance;
	}

	@SubscribeEvent
	public void pauseSounds(GuiOpenEvent event)
	{
		if(event.gui != null && event.gui.getClass().getSimpleName().equals("GuiIngameMenu") && Minecraft.getMinecraft().isSingleplayer())
		{
			SoundPlayer.getInstance().pauseSounds();
			paused = true;
		}
		else if(event.gui == null && paused == true)
		{
			SoundPlayer.getInstance().resumeSounds();
			paused = false;
		}
	}
}
