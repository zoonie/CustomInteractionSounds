package com.zoonie.interactionsounds.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public interface IListGui
{
	public Minecraft getMinecraftInstance();

	public FontRenderer getFontRenderer();

	public void selectIndex(int selected);

	public boolean indexSelected(int var1);

	public int getWidth();

	public int getHeight();

	public void drawBackground();
}
