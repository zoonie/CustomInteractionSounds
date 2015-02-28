package com.zoonie.custominteractionsounds.gui.mapping;

import net.minecraft.client.renderer.Tessellator;

import com.zoonie.custominteractionsounds.gui.GuiScrollableList;
import com.zoonie.custominteractionsounds.gui.IListGui;
import com.zoonie.custominteractionsounds.sound.Sound;

public class GuiScrollableSoundsList extends GuiScrollableList
{
	private IListGui parent;

	public GuiScrollableSoundsList(IListGui parent, int width, int height, int top, int bottom, int left, int entryHeight)
	{
		super(parent.getMinecraftInstance(), width, height, top, bottom, left, entryHeight);
		this.parent = parent;
	}

	@Override
	protected int getSize()
	{
		return GuiInteractionSoundMapping.sounds.size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2)
	{
		parent.selectIndex(var1);
	}

	@Override
	protected boolean isSelected(int var1)
	{
		return parent.indexSelected(var1);
	}

	@Override
	protected void drawBackground()
	{
		parent.drawBackground();
	}

	@Override
	protected int getContentHeight()
	{
		return (getSize()) * super.slotHeight + 1;
	}

	@Override
	protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
	{
		Sound sound = GuiInteractionSoundMapping.sounds.get(listIndex);
		if(sound != null)
		{
			parent.getFontRenderer().drawString(parent.getFontRenderer().trimStringToWidth(sound.getSoundName(), listWidth - 10), this.left + 3, var3 + 2, 0x00BBFF);
		}
	}
}
