package com.zoonie.InteractionSounds.interaction;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import com.zoonie.InteractionSounds.InteractionSounds;

public class KeyBindings
{
	public static KeyBinding recordInteraction, listSavedInteractions;

	public static void init()
	{
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.KEY_R, InteractionSounds.MOD_NAME);
		listSavedInteractions = new KeyBinding("Interaction->Sound List", Keyboard.KEY_L, InteractionSounds.MOD_NAME);

		ClientRegistry.registerKeyBinding(recordInteraction);
		ClientRegistry.registerKeyBinding(listSavedInteractions);
	}
}
