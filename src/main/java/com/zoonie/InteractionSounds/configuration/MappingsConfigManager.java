package com.zoonie.InteractionSounds.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zoonie.InteractionSounds.InteractionSounds;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.handler.event.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class MappingsConfigManager
{
	private static File config;

	public MappingsConfigManager(File config)
	{
		this.config = config;
		read();
	}

	private void read()
	{
		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		try
		{
			if(!config.exists())
			{
				config.getParentFile().mkdirs();
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
					Sound foundSound = SoundHandler.getSound(new SoundInfo(soundInfo.getSoundName(), soundInfo.getCategory()));
					if(foundSound != null)
					{
						Sound sound = new Sound(foundSound);
						sound.setVolume(soundInfo.getVolume());
						ClientProxy.mappings.put(entry.getKey(), sound);
					}
					else
						InteractionSounds.logger.error("Could not find sound: " + soundInfo.getSoundName() + " within a folder named: " + soundInfo.getCategory());
				}
			}

			br.close();
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public static void write()
	{
		try
		{
			if(!config.exists())
			{
				config.getParentFile().mkdirs();
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
