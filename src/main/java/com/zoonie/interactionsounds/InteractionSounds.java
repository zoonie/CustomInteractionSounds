package com.zoonie.interactionsounds;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zoonie.interactionsounds.gui.GuiHandler;
import com.zoonie.interactionsounds.network.ChannelHandler;
import com.zoonie.interactionsounds.proxy.CommonProxy;

@Mod(modid = InteractionSounds.MODID, name = InteractionSounds.MOD_NAME, version = InteractionSounds.VERSION, acceptableRemoteVersions = "*", guiFactory = "com.zoonie.interactionsounds.gui.GuiFactory")
public class InteractionSounds
{
	public static final String MOD_NAME = "Interaction Sounds";
	public static final String MODID = "InteractionSounds";
	public static final String VERSION = "1.0.3";

	public static final Logger logger = LogManager.getLogger(MOD_NAME);

	@Instance(MODID)
	public static InteractionSounds instance;

	@SidedProxy(clientSide = "com.zoonie.interactionsounds.proxy.ClientProxy", serverSide = "com.zoonie.interactionsounds.proxy.CommonProxy")
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
