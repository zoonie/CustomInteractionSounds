package com.zoonie.InteractionSounds.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.zoonie.InteractionSounds.EventHandlers.Interaction;
import com.zoonie.InteractionSounds.proxy.ClientProxy;

public class ConfigurationManager
{
	private File config;

	public ConfigurationManager(File config) {
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
				if(values.length != 4)
					throw new IOException("config error: " + " on line " + lineNo + " of " + config.getName() + ". Length = " + values.length
							+ " when it should equal 4.");
				ClientProxy.interactions.add(new Interaction(values[0].trim(), values[1].trim(), values[2].trim(), values[3].trim()));
			}
			br.close();
		} catch(IOException e)
		{
			System.err.println(e.getMessage());
		} finally
		{
			try
			{
				br.close();
			} catch(IOException e)
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
			for(Interaction interaction : ClientProxy.interactions)
			{
				bw.write("mapping= ");
				bw.write(interaction.getMouseButton() + " | ");
				bw.write(interaction.getItem() + " | ");
				bw.write(interaction.getTarget() + " | ");
				bw.write(interaction.getSound().getSoundName());
				bw.write("\n");
			}
			bw.close();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
