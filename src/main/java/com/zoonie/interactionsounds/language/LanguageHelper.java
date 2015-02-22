package com.zoonie.interactionsounds.language;

import net.minecraft.util.StatCollector;

public class LanguageHelper
{
	public static String translate(String s0)
	{
		if(StatCollector.canTranslate(s0))
			return StatCollector.translateToLocal(s0);

		String s1 = s0 + ".name";
		if(StatCollector.canTranslate(s1))
			return StatCollector.translateToLocal(s1);

		String s2 = s1.replace("tile.", "item.");
		if(StatCollector.canTranslate(s2))
			return StatCollector.translateToLocal(s2);

		return s0;
	}
}
