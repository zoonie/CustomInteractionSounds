package com.zoonie.InteractionSounds.proxy;

import java.util.HashSet;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import org.lwjgl.input.Keyboard;

import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.EventHandlers.Interaction;
import com.zoonie.InteractionSounds.EventHandlers.InteractionHandler;

public class ClientProxy extends CommonProxy
{
	public static KeyBinding recordInteraction;
	public static HashSet<Interaction> interactions;
	
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(new InteractionHandler());
		
		recordInteraction = new KeyBinding("Record Interaction", Keyboard.KEY_P, InteractionSounds.MOD_NAME);	  
		ClientRegistry.registerKeyBinding(recordInteraction);
	}
}
