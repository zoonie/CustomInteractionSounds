package com.zoonie.InteractionSounds.gui.viewing;

import static com.zoonie.InteractionSounds.helper.LanguageHelper.translate;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import com.zoonie.InteractionSounds.gui.GuiScrollableList;
import com.zoonie.InteractionSounds.gui.IListGui;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;

public class GuiMappingList extends GuiScrollableList
{
	IListGui parent;

	public GuiMappingList(IListGui parent, Minecraft client, int width, int height, int top, int bottom, int left, int entryHeight)
	{
		super(client, width, height, top, bottom, left, entryHeight);
		this.parent = parent;
	}

	@Override
	protected int getSize()
	{
		return ClientProxy.mappings.size();
	}

	@Override
	protected void elementClicked(int var1, boolean var2)
	{
		this.parent.selectIndex(var1);
	}

	@Override
	protected boolean isSelected(int var1)
	{
		return this.parent.indexSelected(var1);
	}

	@Override
	protected void drawBackground()
	{
		this.parent.drawBackground();
	}

	@Override
	protected int getContentHeight()
	{
		return (this.getSize()) * 25 + 1;
	}

	@Override
	protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5)
	{
		Entry<Interaction, Sound> entry = GuiListContainer.mappingsList.get(listIndex);
		Interaction inter = entry.getKey();
		Sound sound = entry.getValue();

		this.parent.getFontRenderer().drawString(translate(inter.getMouseButton()) + "  -  " + translate(inter.getTarget()) + "  -  " + translate(inter.getItem()) + "  -  " + sound.getSoundName(),
				this.left + 3, var3 + 2, 0xFFFFFF);
	}

}
