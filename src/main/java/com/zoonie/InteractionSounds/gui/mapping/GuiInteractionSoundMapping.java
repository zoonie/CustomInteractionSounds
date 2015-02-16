package com.zoonie.InteractionSounds.gui.mapping;

import static com.zoonie.InteractionSounds.language.LanguageHelper.translate;

import java.awt.Component;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import com.zoonie.InteractionSounds.interaction.Interaction;
import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.network.message.RequestSoundMessage;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundInfo;
import com.zoonie.InteractionSounds.sound.SoundPlayer;

public class GuiInteractionSoundMapping extends GuiScreen implements IListGui
{
	private int labelAlign;
	private int infoAlign;

	private GuiScrollableSoundsList soundsList;
	private int selected = -1;
	private Sound selectedSound;
	private JFileChooser fileChooser;
	private EntityPlayer player;
	private GuiButton saveButton, playButton, listButton;
	private String currentlyPlayingSoundId = null;
	private long timeSoundFinishedPlaying;
	private Interaction interaction;
	private Boolean justUploaded = false;
	private GuiCheckBox itemChecked, targetChecked, generalTargetChecked;
	private GuiSlider slider;
	protected static List<Sound> sounds;
	private String soundLength;
	private String soundSize;
	private double soundLengthSeconds;

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
		fileChooser.setFileFilter(new FileNameExtensionFilter(translate("gui.sound.files") + " (.ogg, .wav, .mp3)", "ogg", "wav", "mp3"));
	}

	@Override
	public void initGui()
	{
		super.initGui();
		soundsList = new GuiScrollableSoundsList(this, mc, 140, 0, 30, getHeight() - 25, 10, 18);
		this.buttonList.add(saveButton = new GuiButton(0, getWidth() / 2, getHeight() - 25, 98, 20, translate("gui.mapping.save")));
		saveButton.enabled = false;
		this.buttonList.add(new GuiButton(1, 10, getHeight() - 25, 140, 20, translate("gui.sound.add")));
		this.buttonList.add(playButton = new GuiButton(2, getWidth() / 2, getHeight() - 50, translate("gui.sound.play")));
		playButton.enabled = false;
		this.buttonList.add(new GuiButton(3, getWidth() / 2 + 103, getHeight() - 25, 98, 20, translate("gui.mapping.cancel")));

		this.buttonList.add(itemChecked = new GuiCheckBox(4, (int) (getWidth() / 1.18), getHeight() - 70, " " + translate("interaction.any"), false));
		this.buttonList.add(targetChecked = new GuiCheckBox(5, (int) (getWidth() / 1.18), getHeight() - 95, " " + translate("interaction.any"), false));
		this.buttonList.add(generalTargetChecked = new GuiCheckBox(6, (int) (getWidth() / 1.18), getHeight() - 85, " " + translate("interaction.general"), false));

		labelAlign = (int) (getWidth() * 0.4);
		infoAlign = (int) (getWidth() * 0.53);

		this.buttonList.add(slider = new GuiSlider(7, infoAlign, 95, 100, 20, "", "%", 0, 100, 100, false, true));
		slider.visible = false;

		this.buttonList.add(listButton = new GuiButton(8, 10, 10, 140, 20, translate("gui.sound.list.player")));

		sounds = SoundHandler.getPlayerSounds();
	}

	@Override
	public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
	{
		this.soundsList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
		super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

		if(selectedSound != null)
		{
			selectIndex(selected);
			drawSoundInfo();
			this.drawString(this.getFontRenderer(), translate("sound.volume") + ":", labelAlign, 100, 0xFFFFFF);
			slider.visible = true;
			if(timeSoundFinishedPlaying > 0)
				SoundPlayer.getInstance().adjustVolume(currentlyPlayingSoundId, (float) slider.getValue() / 100);
		}
		else
			slider.visible = false;

		if(playButton != null && playButton.displayString.equals(translate("gui.sound.stop")) && System.currentTimeMillis() > timeSoundFinishedPlaying)
		{
			playButton.displayString = translate("gui.sound.play");
		}

		labelAlign = (int) (getWidth() * 0.4);
		infoAlign = (int) (getWidth() * 0.53);

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
						interaction.setItem("interaction.any");
					if(targetChecked.isChecked() && interaction.isEntity())
						interaction.setTarget("interaction.any.entity");
					if(targetChecked.isChecked() && !interaction.isEntity())
						interaction.setTarget("interaction.any.block");
					if(generalTargetChecked.isChecked())
						interaction.useGeneralTargetName();

					ChannelHandler.network.sendToServer(new RequestSoundMessage(selectedSound.getSoundName(), selectedSound.getCategory(), true));

					selectedSound = new Sound(selectedSound);
					selectedSound.setVolume((float) slider.getValue() / 100);
					MappingsConfigManager.mappings.put(interaction, selectedSound);
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
					{
						Sound sound = new Sound(fileChooser.getSelectedFile());
						if(!SoundHandler.getSounds().containsKey(new SoundInfo(sound.getSoundName(), sound.getCategory())))
						{
							selectedSound = SoundHandler.setupSound(sound.getSoundLocation());
							SoundHandler.addSound(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory()), selectedSound.getSoundLocation());
						}
					}
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
						timeSoundFinishedPlaying = (long) (soundLengthSeconds * 1000) + System.currentTimeMillis();
						currentlyPlayingSoundId = UUID.randomUUID().toString();
						SoundHandler.playSound(new SoundInfo(selectedSound.getSoundName(), selectedSound.getCategory()), currentlyPlayingSoundId, (int) player.posX, (int) player.posY,
								(int) player.posZ, (float) slider.getValue() / 100);
						playButton.displayString = translate("gui.sound.stop");
					}
					else
					{
						timeSoundFinishedPlaying = 0;
						playButton.displayString = translate("gui.sound.play");
						SoundPlayer.getInstance().stopSound(currentlyPlayingSoundId);
						SoundPlayer.getInstance().removeSound(currentlyPlayingSoundId);
					}
				}
				break;
			case 3:
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			case 5:
				generalTargetChecked.setIsChecked(false);
				break;
			case 6:
				targetChecked.setIsChecked(false);
				break;
			case 8:
				if(!listButton.displayString.equals(translate("gui.sound.list.player")))
				{
					sounds = SoundHandler.getPlayerSounds();
					listButton.displayString = translate("gui.sound.list.player");
				}
				else
				{
					sounds = new ArrayList<Sound>(SoundHandler.getSounds().values());
					listButton.displayString = translate("gui.sound.list");
				}
				break;
			}
		}
	}

	private void updateSoundsList()
	{
		if(listButton.displayString.equals(translate("gui.sound.list.player")))
		{
			sounds = SoundHandler.getPlayerSounds();
			listButton.displayString = translate("gui.sound.list.player");
		}
		else
		{
			sounds = new ArrayList<Sound>(SoundHandler.getSounds().values());
			listButton.displayString = translate("gui.sound.list");
		}
	}

	public void onSelectedSoundChanged()
	{
		if(selectedSound != null)
		{
			if(selectedSound.hasLocal())
			{
				saveButton.enabled = true;
				playButton.enabled = true;
				if(!playButton.displayString.equals(translate("gui.sound.stop")))
					playButton.displayString = translate("gui.sound.play");
				soundLengthSeconds = SoundHelper.getSoundLength(selectedSound.getSoundLocation());
				soundSize = FileUtils.byteCountToDisplaySize(selectedSound.getSoundLocation().length());
			}
			else
			{
				saveButton.enabled = false;
				playButton.enabled = true;
				if(!playButton.displayString.equals(translate("gui.sound.stop")))
					playButton.displayString = translate("gui.sound.download") + "/" + translate("gui.sound.play");
				soundLengthSeconds = selectedSound.getLength();
				soundSize = FileUtils.byteCountToDisplaySize(selectedSound.getSize());
			}
			long milliseconds = (long) (soundLengthSeconds * 100) % 100;
			long minutes = TimeUnit.SECONDS.toMinutes((long) soundLengthSeconds) - (TimeUnit.SECONDS.toHours((long) soundLengthSeconds) * 60);
			long seconds = TimeUnit.SECONDS.toSeconds((long) soundLengthSeconds) - (TimeUnit.SECONDS.toMinutes((long) soundLengthSeconds) * 60);
			soundLength = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
		}
		else
		{
			saveButton.enabled = false;
			playButton.enabled = false;
		}
	}

	private void drawSoundInfo()
	{
		this.drawString(this.getFontRenderer(), translate("sound.name") + ":", labelAlign, 15, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), selectedSound.getSoundName(), infoAlign, 15, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("sound.folder") + ":", labelAlign, 35, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), selectedSound.getCategory(), infoAlign, 35, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("sound.length") + ":", labelAlign, 55, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), soundLength, infoAlign, 55, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("sound.size") + ":", labelAlign, 75, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), soundSize, infoAlign, 75, 0xFFFFFF);
	}

	private void drawInteractionInfo()
	{
		String targetAny = interaction.isEntity() ? translate("interaction.any.entity") : translate("interaction.any.block");
		String item = itemChecked.isChecked() ? translate("interaction.any") : translate(interaction.getItem());
		String preTarget = generalTargetChecked.isChecked() ? translate(interaction.getGeneralTargetName()) : translate(interaction.getTarget());
		String target = targetChecked.isChecked() ? targetAny : preTarget;

		this.drawString(this.getFontRenderer(), translate("interaction.mouse") + ":", labelAlign, getHeight() - 110, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), translate("interaction.mouse." + interaction.getMouseButton()), infoAlign, getHeight() - 110, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("interaction.target") + ":", labelAlign, getHeight() - 90, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), getFontRenderer().trimStringToWidth(target, (int) ((getWidth() / 1.18) - infoAlign)), infoAlign, getHeight() - 90, 0xFFFFFF);

		this.drawString(this.getFontRenderer(), translate("interaction.item") + ":", labelAlign, getHeight() - 70, 0xFFFFFF);
		this.drawString(this.getFontRenderer(), getFontRenderer().trimStringToWidth(item, (int) ((getWidth() / 1.18) - infoAlign)), infoAlign, getHeight() - 70, 0xFFFFFF);
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
		updateSoundsList();
		this.selected = selected;
		if(selected >= 0 && selected < sounds.size())
		{
			this.selectedSound = sounds.get(selected);
			onSelectedSoundChanged();
		}
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
			SoundPlayer.getInstance().stopSound(currentlyPlayingSoundId);
			SoundPlayer.getInstance().stopSound(currentlyPlayingSoundId);
		}
	}
}
