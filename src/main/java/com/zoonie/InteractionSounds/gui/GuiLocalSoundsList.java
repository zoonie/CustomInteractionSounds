package com.zoonie.InteractionSounds.gui;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;

public class GuiLocalSoundsList extends GuiScrollingList
{
	private IListGui parent;

	public GuiLocalSoundsList(IListGui parent, int listWidth) {
		super(parent.getMinecraftInstance(), listWidth, parent.getHeight() + 50, 10, parent.getHeight() - 44, 10, 18);
		this.parent = parent;
	}

	@Override
	protected int getSize()
	{
		return SoundHandler.getLocalSounds().size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2)
	{
		this.parent.selectSoundIndex(var1);
	}

	@Override
	protected boolean isSelected(int var1)
	{
		return this.parent.soundIndexSelected(var1);
	}

	@Override
	protected void drawBackground()
	{
		this.parent.drawBackground();
	}

	@Override
	protected int getContentHeight()
	{
		return (this.getSize()) * 18 + 1;
	}

	@Override
	protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
	{
		Sound sound = SoundHandler.getLocalSounds().get(listIndex);
		if(sound != null && var3 + 18 < (parent.getHeight() - 35) && var3 > 7)
		{
			this.parent.getFontRenderer().drawString(this.parent.getFontRenderer().trimStringToWidth(sound.getSoundName(), listWidth - 10), this.left + 3,
					var3 + 2, 0xFFFFFF);
		}
	}
}
