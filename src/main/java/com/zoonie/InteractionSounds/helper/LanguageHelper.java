package com.zoonie.InteractionSounds.helper;

import net.minecraft.util.StatCollector;

import com.zoonie.InteractionSounds.InteractionSounds;

public class LanguageHelper
{
	public static String translate(String s)
	{
		if(StatCollector.canTranslate(s + ".name"))
			return StatCollector.translateToLocal(s + ".name");
		else if(StatCollector.canTranslate(InteractionSounds.MODID + "." + s))
			return StatCollector.translateToLocal(InteractionSounds.MODID + "." + s);
		else
			return s;
	}
}
