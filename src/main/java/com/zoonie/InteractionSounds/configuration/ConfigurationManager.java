package com.zoonie.InteractionSounds.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class ConfigurationManager
{
	private File config;

	public ConfigurationManager(File config)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			this.config = config;
			this.read();
		}
	}

	private void read()
	{
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		try
		{
			if(!config.exists())
			{
				config.createNewFile();
			}
			BufferedReader br = new BufferedReader(new FileReader(config));
			Type type = new TypeToken<HashMap<Interaction, Sound>>() {
			}.getType();
			HashMap<Interaction, Sound> mappings = gson.fromJson(br, type);

			if(mappings != null)
			{
				for(Entry<Interaction, Sound> entry : mappings.entrySet())
				{
					Sound soundInfo = entry.getValue();
					Sound sound = new Sound(SoundHandler.getSound(new SoundInfo(soundInfo.getSoundName(), soundInfo.getCategory())));
					sound.setVolume(soundInfo.getVolume());
					ClientProxy.mappings.put(entry.getKey(), sound);
				}
			}

			br.close();
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public void writeAll()
	{
		try
		{
			if(!config.exists())
			{
				config.createNewFile();
			}

			Gson gson = new GsonBuilder().enableComplexMapKeySerialization().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

			String json = gson.toJson(ClientProxy.mappings);

			FileWriter writer = new FileWriter(config.getAbsoluteFile());
			writer.write(json);
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
