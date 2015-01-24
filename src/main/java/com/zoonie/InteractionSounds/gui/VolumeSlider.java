package com.zoonie.InteractionSounds.gui;

import net.minecraftforge.fml.client.config.GuiSlider;

public class VolumeSlider extends GuiSlider
{

	public VolumeSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal,
			boolean showDec, boolean drawStr) {
		super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr);
	}

}
