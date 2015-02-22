package com.zoonie.interactionsounds.interaction;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import com.zoonie.interactionsounds.InteractionSounds;

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
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.KEY_R, InteractionSounds.MOD_NAME);
		listSavedInteractions = new KeyBinding("Interaction->Sound List", Keyboard.KEY_L, InteractionSounds.MOD_NAME);
	}

	public static void deInit()
	{
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.CHAR_NONE, InteractionSounds.MOD_NAME);
		listSavedInteractions = new KeyBinding("Interaction->Sound List", Keyboard.CHAR_NONE, InteractionSounds.MOD_NAME);
	}
}
