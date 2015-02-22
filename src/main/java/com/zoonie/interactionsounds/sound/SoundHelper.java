package com.zoonie.interactionsounds.sound;

import java.io.File;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;

public class SoundHelper
{
	public static double getSoundLength(File soundFile)
	{
		try
		{
			AudioHeader audioHeader = AudioFileIO.read(soundFile).getAudioHeader();
			if(soundFile.getName().endsWith(".mp3"))
			{
				return ((MP3AudioHeader) audioHeader).getPreciseTrackLength();
			}
			else
			{
				return ((GenericAudioHeader) audioHeader).getPreciseLength();
			}
		}
		catch(Exception e)
		{
			//ignore
		}
		return 0;
	}
}
