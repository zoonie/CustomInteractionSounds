package com.zoonie.InteractionSounds.gui.mapping;

import static com.zoonie.InteractionSounds.helper.LanguageHelper.translate;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiSlider;

import org.apache.commons.io.FileUtils;

import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.gui.IListGui;
import com.zoonie.InteractionSounds.handler.ChannelHandler;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.helper.SoundHelper;
import com.zoonie.InteractionSounds.network.packet.client.RequestSoundMessage;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundInfo;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class GuiInteractionSoundMapping extends GuiScreen implements IListGui
{
	private GuiScrollableSoundsList soundsList;
	private int selected = -1;
	private Sound selectedSound;
	private JFileChooser fileChooser;
	private EntityPlayer player;
	private GuiButton saveButton, playButton, listButton;
	private UUID currentlyPlayerSoundId;
	private long timeSoundFinishedPlaying;
	private Interaction interaction;
	private Boolean justUploaded = false;
	private GuiCheckBox itemChecked, targetChecked, generalTargetChecked;
	private GuiSlider slider;
	protected static List<Sound> sounds;

	public GuiInteractionSoundMapping(EntityPlayer player, Interaction interaction)
	{
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
		fileChooser.setFileFilter(new FileNameExtensionFilter(translate("soundFiles") + " (.ogg, .wav, .mp3)", "ogg", "wav", "mp3"));
	}

	@Override
	public void initGui()
	{
		super.initGui();
		soundsList = new GuiScrollableSoundsList(this, mc, 140, 0, 30, getHeight() - 25, 10, 18);
		this.buttonList.add(saveButton = new GuiButton(0, getWidth() / 2, getHeight() - 25, 98, 20, translate("interaction.save")));
		saveButton.enabled = false;
		this.buttonList.add(new GuiButton(1, 10, getHeight() - 25, 140, 20, translate("sound.selectFile")));
		this.buttonList.add(playButton = new GuiButton(2, getWidth() / 2, getHeight() - 50, translate("sound.play")));
		playButton.enabled = false;
		this.buttonList.add(new GuiButton(3, getWidth() / 2 + 103, getHeight() - 25, 98, 20, translate("interaction.cancel")));

		this.buttonList.add(itemChecked = new GuiCheckBox(4, (int) (getWidth() / 1.2), getHeight() - 70, " " + translate("interaction.any"), false));
		this.buttonList.add(targetChecked = new GuiCheckBox(5, (int) (getWidth() / 1.2), getHeight() - 95, " " + translate("interaction.any"), false));
		this.buttonList.add(generalTargetChecked = new GuiCheckBox(6, (int) (getWidth() / 1.2), getHeight() - 85, " " + translate("interaction.general"), false));

		this.buttonList.add(slider = new GuiSlider(7, (int) (getWidth() / 1.95), 75, 100, 20, "", "%", 0, 100, 100, false, true));
		slider.visible = false;

		this.buttonList.add(listButton = new GuiButton(8, 10, 10, 140, 20, translate("sound.playerList")));

		sounds = SoundHandler.getPlayerSounds();
	}

	@Override
	public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
	{
		this.soundsList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
		super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

		if(selectedSound != null)
		{
			drawSoundInfo();
			slider.visible = true;
		}
		else
			slider.visible = false;

		if(playButton != null && playButton.displayString.equalsIgnoreCase(translate("sound.stop")) && System.currentTimeMillis() > timeSoundFinishedPlaying)
		{
			playButton.displayString = translate("sound.play");
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
					if(targetChecked.isChecked() && interaction.isEntity())
						interaction.setTarget("any.entity");
					if(targetChecked.isChecked() && !interaction.isEntity())
						interaction.setTarget("any.block");
					if(generalTargetChecked.isChecked())
						interaction.useGeneralTargetName();

					if(!SoundHandler.getSounds().containsKey(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory())))
					{
						selectedSound = SoundHandler.setupSound(selectedSound.getSoundLocation());
						SoundHandler.addSound(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory()), selectedSound.getSoundLocation());
					}
					selectedSound = new Sound(selectedSound);

					ChannelHandler.network.sendToServer(new RequestSoundMessage(selectedSound.getSoundName(), selectedSound.getCategory(), true));

					selectedSound.setVolume((float) slider.getValue() / 100);
					ClientProxy.mappings.put(interaction, selectedSound);
					MappingsConfigManager.write();
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
					selectIndex(-1);
					if(fileChooser.getSelectedFile().exists())
						selectedSound = new Sound(fileChooser.getSelectedFile());
					else
						selectedSound = null;
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
						SoundPlayer.playSound(selectedSound.getSoundLocation(), currentlyPlayerSoundId.toString(), (float) player.posX, (float) player.posY, (float) player.posZ, false,
								(float) slider.getValue() / 100);
						playButton.displayString = translate("sound.stop");
					}
					else
					{
						timeSoundFinishedPlaying = 0;
						playButton.displayString = translate("sound.play");
						SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
					}
				}
				break;
			case 3:
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			case 8:
				if(!listButton.displayString.equals(translate("sound.playerList")))
				{
					sounds = SoundHandler.getPlayerSounds();
					listButton.displayString = translate("sound.playerList");
				}
				else
				{
					sounds = new ArrayList<Sound>(SoundHandler.getSounds().values());
					listButton.displayString = translate("sound.list");
				}
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

	private void drawSoundInfo()
	{
		this.drawString(this.getFontRenderer(), translate("sound.name") + ":", (int) (getWidth() / 2.45), 15, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), selectedSound.getSoundName(), (int) (getWidth() / 1.95), 15, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("sound.folder") + ":", (int) (getWidth() / 2.45), 35, 0xFFFFFF);
		if(selectedSound.getCategory() != null)
		{
			this.drawString(this.getFontRenderer(), selectedSound.getCategory(), (int) (getWidth() / 1.95), 35, 0xFFFFFF);
		}

		this.drawString(this.getFontRenderer(), translate("sound.size") + ":", (int) (getWidth() / 2.45), 55, 0xFFFFFF);
		if(selectedSound.getSoundLocation() != null)
		{
			String space = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
			this.drawString(this.getFontRenderer(), space, (int) (getWidth() / 1.95), 55, 0xFFFFFF);
		}

		this.drawString(this.getFontRenderer(), translate("sound.volume") + ":", (int) (getWidth() / 2.45), 80, 0xFFFFFF);

		if(timeSoundFinishedPlaying > 0)
			SoundPlayer.adjustVolume(currentlyPlayerSoundId.toString(), (float) slider.getValue() / 100);
	}

	private void drawInteractionInfo()
	{
		String targetAny = interaction.isEntity() ? translate("any.entity") : translate("any.block");
		String item = itemChecked.isChecked() ? translate("interaction.any") : translate(interaction.getItem());
		String preTarget = generalTargetChecked.isChecked() ? translate(interaction.getGeneralTargetName()) : translate(interaction.getTarget());
		String target = targetChecked.isChecked() ? targetAny : preTarget;

		this.drawString(this.getFontRenderer(), translate("interaction.mouse") + ":", (int) (getWidth() / 2.45), getHeight() - 110, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), translate(interaction.getMouseButton()), (int) (getWidth() / 1.95), getHeight() - 110, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("interaction.target") + ":", (int) (getWidth() / 2.45), getHeight() - 90, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), target, (int) (getWidth() / 1.95), getHeight() - 90, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("interaction.item") + ":", (int) (getWidth() / 2.45), getHeight() - 70, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), item, (int) (getWidth() / 1.95), getHeight() - 70, 0xFFFFFF);
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
	public void selectIndex(int selected)
	{
		this.selected = selected;
		if(selected >= 0 && selected < SoundHandler.getSounds().size())
		{
			this.selectedSound = sounds.get(selected);
		}
		onSelectedSoundChanged();
	}

	@Override
	public boolean indexSelected(int var1)
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
	public void onGuiClosed()
	{
		if(System.currentTimeMillis() < timeSoundFinishedPlaying)
		{
			SoundPlayer.stopSound(currentlyPlayerSoundId.toString());
		}
	}
}
