package com.zoonie.custominteractionsounds.interaction;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import com.zoonie.custominteractionsounds.CustomInteractionSounds;

import cpw.mods.fml.client.registry.ClientRegistry;

public class KeyBindings
{
	public static KeyBinding recordInteraction, listSavedInteractions;

	public static void init()
	{
		assign();

		ClientRegistry.registerKeyBinding(recordInteraction);
		ClientRegistry.registerKeyBinding(listSavedInteractions);
	}

	public static void assign()
	{
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.KEY_R, CustomInteractionSounds.MOD_NAME);
		listSavedInteractions = new KeyBinding("Interaction->Sound List", Keyboard.KEY_L, CustomInteractionSounds.MOD_NAME);
	}

	public static void deInit()
	{
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.CHAR_NONE, CustomInteractionSounds.MOD_NAME);
		listSavedInteractions = new KeyBinding("Interaction->Sound List", Keyboard.CHAR_NONE, CustomInteractionSounds.MOD_NAME);
	}
}
