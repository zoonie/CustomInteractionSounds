package com.zoonie.custominteractionsounds.gui.viewing;

import static com.zoonie.custominteractionsounds.language.LanguageHelper.translate;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.entity.player.EntityPlayer;

import com.zoonie.custominteractionsounds.configuration.MappingsConfigManager;
import com.zoonie.custominteractionsounds.gui.IListGui;
import com.zoonie.custominteractionsounds.interaction.Interaction;
import com.zoonie.custominteractionsounds.sound.Sound;

public class GuiListContainer extends GuiScreen implements IListGui, GuiYesNoCallback
{
	private EntityPlayer player;
	private int selected = -1;
	private GuiMappingList mappingList;
	protected static ArrayList<Entry<Interaction, Sound>> mappingsList;

	public GuiListContainer(EntityPlayer player)
	{
		this.player = player;
		mappingsList = new ArrayList<Entry<Interaction, Sound>>(MappingsConfigManager.mappings.entrySet());
	}

	@Override
	public void initGui()
	{
		super.initGui();
		mappingList = new GuiMappingList(this, mc, getWidth() - 20, 0, 15, getHeight() - 25, 10, 20);
		this.buttonList.add(new GuiButton(0, (int) (getWidth() / 2) - 155, getHeight() - 23, 140, 20, translate("gui.mapping.delete")));
		this.buttonList.add(new GuiButton(1, (int) (getWidth() / 2) + 15, getHeight() - 23, 140, 20, translate("gui.mapping.cancel")));
	}

	@Override
	public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
	{
		this.mappingList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
		super.drawScreen(p_571_1_, p_571_2_, p_571_3_);

		getFontRenderer().drawString(translate("interaction.mouse"), 13, 5, 0xFFFFFF);
		getFontRenderer().drawString(translate("interaction.target"), (int) (getWidth() * 0.14), 5, 0xFFFFFF);
		getFontRenderer().drawString(translate("interaction.item"), (int) (getWidth() * 0.42), 5, 0xFFFFFF);
		getFontRenderer().drawString(translate("sound.name"), (int) (getWidth() * 0.7), 5, 0xFFFFFF);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button.enabled)
		{
			switch(button.id)
			{
			case 0:
				if(selected >= 0)
					mc.displayGuiScreen(new GuiYesNo(this, null, translate("gui.delete.confirm"), selected));
				break;
			case 1:
				this.mc.displayGuiScreen(null);
				this.mc.setIngameFocus();
				break;
			}
		}
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

	public void confirmClicked(boolean result, int id)
	{
		if(result)
		{
			mappingsList.remove(selected);
			MappingsConfigManager.mappings = new TreeMap<Interaction, Sound>();
			for(Entry<Interaction, Sound> entry : mappingsList)
			{
				MappingsConfigManager.mappings.put(entry.getKey(), entry.getValue());
			}
			MappingsConfigManager.write();
			selected = -1;
		}
		mc.displayGuiScreen(this);
	}
}
