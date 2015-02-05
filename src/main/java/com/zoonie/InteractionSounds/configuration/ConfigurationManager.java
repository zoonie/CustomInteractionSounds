package com.zoonie.InteractionSounds.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

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
		BufferedReader br = null;
		try
		{
			if(!config.exists())
			{
				config.createNewFile();
			}
			br = new BufferedReader(new FileReader(config));
			String line;
			int lineNo = 0;
			while((line = br.readLine()) != null)
			{
				lineNo++;

				int i = line.indexOf('=');
				String value = line.substring(i + 1);

				String[] values = value.split("\\|");
				if(values.length != 6)
					throw new IOException("Config error: on line " + lineNo + " of " + config.getName() + ". Length = " + values.length + " when it should equal 6.");
				Interaction interaction = new Interaction(values[0].trim(), values[1].trim(), values[2].trim());
				Sound sound = SoundHandler.getSounds().get(new SoundInfo(values[3].trim(), values[4].trim()));
				if(sound != null)
					sound.setVolume((float) Float.parseFloat(values[5].trim()));
				else
					throw new IOException("Config error: on line " + lineNo + " of " + config.getName() + ".  Check that sound file: \"" + values[3].trim()
							+ "\" exists within sounds folder of your Minecraft installation folder.");
				ClientProxy.mappings.put(interaction, sound);
			}
			br.close();
		}
		catch(IOException e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				br.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
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
			FileWriter fw = new FileWriter(config.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(Map.Entry<Interaction, Sound> entry : ClientProxy.mappings.entrySet())
			{
				Interaction interaction = entry.getKey();
				Sound sound = entry.getValue();
				bw.write("mapping= ");
				bw.write(interaction.getMouseButton() + " | ");
				bw.write(interaction.getItem() + " | ");
				bw.write(interaction.getTarget() + " | ");
				bw.write(sound.getSoundName() + " | ");
				bw.write(sound.getCategory() + " | ");
				bw.write(Float.toString(sound.getVolume()));
				bw.write("\n");
			}
			bw.close();
			fw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
