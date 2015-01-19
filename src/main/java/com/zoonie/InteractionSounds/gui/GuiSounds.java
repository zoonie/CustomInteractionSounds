package com.zoonie.InteractionSounds.gui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import org.apache.commons.io.FileUtils;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.EventHandlers.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class GuiSounds extends GuiScreen implements IListGui
{
	private GuiLocalSoundsList soundsList;
	private int selected = -1;
	private Sound selectedSound;
	private JFileChooser fileChooser;
	private EntityPlayer player;
	private GuiButton saveButton;
	private GuiButton playButton;
	private UUID currentlyPlayerSoundId;
	private long timeSoundFinishedPlaying;
	private Interaction interaction;
	private Boolean justUploaded = false;
	private GuiCheckBox itemChecked;
	private GuiCheckBox targetChecked;
	private GuiCheckBox variantChecked;

	public GuiSounds(EntityPlayer player, Interaction interaction) {
		this.player = player;
		this.interaction = interaction;
		fileChooser = new JFileChooser(Minecraft.getMinecraft().mcDataDir) {
			@Override
			protected JDialog createDialog(Component parent) throws HeadlessException
			{
				JDialog dialog = super.createDialog(parent);
				dialog.setLocationByPlatform(true);
				dialog.setAlwaysOnTop(true);
				return dialog;
			}
		};
		fileChooser.setFileFilter(new FileNameExtensionFilter("Sound Files (.ogg, .wav, .mp3)", "ogg", "wav", "mp3"));
		// NetworkHelper.syncPlayerSounds(player);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui()
	{
		super.initGui();
		soundsList = new GuiLocalSoundsList(this, 140);
		this.buttonList.add(saveButton = new GuiButton(0, getWidth() / 2, getHeight() - 42, 98, 20, "Save"));
		saveButton.enabled = false;
		this.buttonList.add(new GuiButton(1, 10, getHeight() - 42, 140, 20, "Select File"));
		this.buttonList.add(playButton = new GuiButton(2, getWidth() / 2, getHeight() - 65, "Play"));
		playButton.enabled = false;
		this.buttonList.add(new GuiButton(3, getWidth() / 2 + 103, getHeight() - 42, 98, 20, "Cancel"));

		this.buttonList.add(itemChecked = new GuiCheckBox(4, (int) (getWidth() / 2.46), 140, " Any item in hand", false));
		this.buttonList.add(targetChecked = new GuiCheckBox(5, (int) (getWidth() / 2.46), 155, " Any target block/entity", false));
		this.buttonList.add(variantChecked = new GuiCheckBox(6, (int) (getWidth() / 2.46), 125, " Use variant", false));
	}

	@Override
	public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
	{
		try
		{
			this.soundsList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
		} catch(Exception ignored)
		{
		}
		super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

		if(selectedSound != null)
		{
			drawSongInfo();
		}
		if(playButton != null && playButton.displayString.equalsIgnoreCase("Stop Sound") && System.currentTimeMillis() > timeSoundFinishedPlaying)
		{
			playButton.displayString = "Play Sound";
		}
		drawInteractionInfo();
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.enabled)
		{
			switch(button.id)
			{
			case 0:
				if(selectedSound != null)
				{
					if(itemChecked.isChecked())
						interaction.setItem("any");
					if(targetChecked.isChecked())
						interaction.setTarget("any");
					if(variantChecked.isChecked())
						interaction.useVariant();
					ClientProxy.mappings.put(interaction, selectedSound);
					InteractionSounds.config.writeAll();

					selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
					// NetworkHelper.clientSoundUpload(sound);
					selectSoundIndex(SoundHandler.getLocalSounds().indexOf(selectedSound));
				}
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			case 1:
				boolean fullscreen = Minecraft.getMinecraft().isFullScreen();
				if(fullscreen)
					Minecraft.getMinecraft().toggleFullscreen();
				int fcReturn = fileChooser.showOpenDialog(null);
				if(fullscreen)
					Minecraft.getMinecraft().toggleFullscreen();
				if(fcReturn == JFileChooser.APPROVE_OPTION)
				{
					selectSoundIndex(-1);
					selectedSound = new Sound(fileChooser.getSelectedFile());
					onSelectedSoundChanged();
				}
				break;
			case 2:
				if(selectedSound != null)
				{
					if(System.currentTimeMillis() > timeSoundFinishedPlaying)
					{
						currentlyPlayerSoundId = UUID.randomUUID();
						timeSoundFinishedPlaying = (long) (SoundHelper.getSoundLength(selectedSound.getSoundLocation()) * 1000) + System.currentTimeMillis();
						SoundPlayer.playSound(selectedSound.getSoundLocation(), currentlyPlayerSoundId.toString(), (float) player.posX, (float) player.posY,
								(float) player.posZ, false);
						playButton.displayString = "Stop Sound";
					}
					else
					{
						timeSoundFinishedPlaying = 0;
						playButton.displayString = "Play Sound";
						SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
					}
				}
				break;
			case 3:
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			}
		}
	}

	public void onSelectedSoundChanged()
	{
		if(selectedSound != null)
		{
			saveButton.enabled = true;
			playButton.enabled = true;
		}
		else
		{
			saveButton.enabled = false;
			playButton.enabled = false;
		}
	}

	private void drawSongInfo()
	{
		this.drawString(this.getFontRenderer(), "Name:", (int) (getWidth() / 2.45), 15, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), selectedSound.getSoundName(),
				getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(selectedSound.getSoundName()) / 2), 15, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), "Folder:", (int) (getWidth() / 2.45), 35, 0xFFFFFF);
		if(selectedSound.getCategory() != null)
		{
			this.drawString(this.getFontRenderer(), selectedSound.getCategory(),
					getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(selectedSound.getCategory()) / 2), 35, 0xFFFFFF);
		}

		this.drawString(this.getFontRenderer(), "Status:", (int) (getWidth() / 2.45), 55, 0xFFFFFF);
		String uploaded = selectedSound.saved() ? "Saved" : "Not saved";
		this.getFontRenderer().drawString(uploaded, getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(uploaded) / 2), 55,
				selectedSound.saved() ? 0x00FF00 : 0xFF0000);

		this.drawString(this.getFontRenderer(), "Size:", (int) (getWidth() / 2.45), 75, 0xFFFFFF);
		if(selectedSound.getSoundLocation() != null)
		{
			String space = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
			this.drawString(this.getFontRenderer(), space, getWidth() / 2 + 100 - (this.getFontRenderer().getStringWidth(space) / 2), 75, 0xFFFFFF);
		}
	}

	private void drawInteractionInfo()
	{
		String variant = variantChecked.isChecked() ? interaction.getVariant() : "";
		String target = targetChecked.isChecked() ? "any target" : StatCollector.translateToLocal(interaction.getTarget() + variant + ".name");
		String item = itemChecked.isChecked() ? "any item" : StatCollector.translateToLocal(interaction.getItem() + ".name");
		String interString = interaction.getMouseButton() + " clicked " + target + " using " + item;
		this.drawString(this.getFontRenderer(), interString, (int) (getWidth() / 2.45), 110, 0xFFFFFF);

	}

	@Override
	public Minecraft getMinecraftInstance()
	{
		return mc;
	}

	@Override
	public FontRenderer getFontRenderer()
	{
		return fontRendererObj;
	}

	@Override
	public void selectSoundIndex(int selected)
	{
		this.selected = selected;
		if(selected >= 0 && selected < SoundHandler.getLocalSounds().size())
		{
			this.selectedSound = SoundHandler.getLocalSounds().get(selected);
		}
		onSelectedSoundChanged();
	}

	@Override
	public boolean soundIndexSelected(int var1)
	{
		return var1 == selected;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public void drawBackground()
	{
		drawDefaultBackground();
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	@Override
	public void onGuiClosed()
	{
		if(System.currentTimeMillis() < timeSoundFinishedPlaying)
		{
			SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
		}
	}
}
