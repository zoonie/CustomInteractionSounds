package com.zoonie.InteractionSounds.gui.viewing;

import static com.zoonie.InteractionSounds.language.LanguageHelper.translate;

import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import com.zoonie.InteractionSounds.configuration.MappingsConfigManager;
import com.zoonie.InteractionSounds.gui.GuiScrollableList;
import com.zoonie.InteractionSounds.gui.IListGui;
import com.zoonie.InteractionSounds.interaction.Interaction;
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
		return MappingsConfigManager.mappings.size();
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
		if(var3 < super.bottom - 9 && var3 > super.top - 3)
		{
			Entry<Interaction, Sound> entry = GuiListContainer.mappingsList.get(listIndex);
			Interaction inter = entry.getKey();
			Sound sound = entry.getValue();

			int width = (int) (parent.getWidth() * 0.28);
			int colour = 0x00BBFF;

			parent.getFontRenderer().drawString(translate("interaction.mouse." + inter.getMouseButton()), this.left + 3, var3 + 2, colour);
			parent.getFontRenderer().drawString(trim(translate(inter.getTarget()), width), (int) (parent.getWidth() * 0.14), var3 + 2, colour);
			parent.getFontRenderer().drawString(trim(translate(inter.getItem()), width), (int) (parent.getWidth() * 0.42), var3 + 2, colour);
			parent.getFontRenderer().drawString(trim(sound.getSoundName(), width), (int) (parent.getWidth() * 0.7), var3 + 2, colour);
		}
	}

	private String trim(String s, int width)
	{
		return parent.getFontRenderer().trimStringToWidth(s, width);
	}
}
