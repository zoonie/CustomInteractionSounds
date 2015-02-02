package com.zoonie.InteractionSounds.gui.viewing;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import com.zoonie.InteractionSounds.gui.IListGui;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;

public class GuiListContainer extends GuiScreen implements IListGui
{
	private EntityPlayer player;
	private int selected = -1;
	private GuiMappingList mappingList;
	protected static ArrayList<Entry<Interaction, Sound>> mappingsList;

	public GuiListContainer(EntityPlayer player)
	{
		this.player = player;
		mappingsList = new ArrayList<Entry<Interaction, Sound>>(ClientProxy.mappings.entrySet());
	}

	@Override
	public void initGui()
	{
		super.initGui();
		mappingList = new GuiMappingList(this, mc, getWidth() - 20, 0, 10, getHeight() - 10, 10, 25);
	}

	@Override
	public void drawScreen(int p_571_1_, int p_571_2_, float p_571_3_)
	{
		this.mappingList.drawScreen(p_571_1_, p_571_2_, p_571_3_);
		super.drawScreen(p_571_1_, p_571_2_, p_571_3_);
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
}
