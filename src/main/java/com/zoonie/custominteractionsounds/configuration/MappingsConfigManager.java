package com.zoonie.custominteractionsounds.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zoonie.custominteractionsounds.CustomInteractionSounds;
import com.zoonie.custominteractionsounds.interaction.Interaction;
import com.zoonie.custominteractionsounds.sound.Sound;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundInfo;

public class MappingsConfigManager
{
	private static File config;
	public static TreeMap<Interaction, Sound> mappings = new TreeMap<Interaction, Sound>();

	public MappingsConfigManager(File config)
	{
		this.config = config;
		read();
	}

	public static void read()
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
			Type type = new TypeToken<TreeMap<Interaction, Sound>>() {}.getType();
			TreeMap<Interaction, Sound> mappings = gson.fromJson(br, type);

			MappingsConfigManager.mappings = new TreeMap<Interaction, Sound>();
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
						MappingsConfigManager.mappings.put(entry.getKey(), sound);
					}
					else
						CustomInteractionSounds.logger.error("Could not find sound: " + soundInfo.getSoundName() + " within a folder named: " + soundInfo.getCategory());
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

			String json = gson.toJson(MappingsConfigManager.mappings);

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
