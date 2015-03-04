package com.zoonie.custominteractionsounds;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zoonie.custominteractionsounds.gui.GuiHandler;
import com.zoonie.custominteractionsounds.network.ChannelHandler;
import com.zoonie.custominteractionsounds.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = CustomInteractionSounds.MODID, name = CustomInteractionSounds.MOD_NAME, version = CustomInteractionSounds.VERSION, acceptableRemoteVersions = "*", guiFactory = "com.zoonie.custominteractionsounds.gui.GuiFactory")
public class CustomInteractionSounds
{
	public static final String MOD_NAME = "Custom Interaction Sounds";
	public static final String MODID = "CustomInteractionSounds";
	public static final String VERSION = "1.0.4";

	public static final Logger logger = LogManager.getLogger(MOD_NAME);

	@Instance(MODID)
	public static CustomInteractionSounds instance;

	@SidedProxy(clientSide = "com.zoonie.custominteractionsounds.proxy.ClientProxy", serverSide = "com.zoonie.custominteractionsounds.proxy.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.preInit(event);

		ChannelHandler.init();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
	}
}
