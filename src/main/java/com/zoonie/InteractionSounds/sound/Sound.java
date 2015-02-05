package com.zoonie.InteractionSounds.sound;

import java.io.File;

import com.google.gson.annotations.Expose;
import com.zoonie.InteractionSounds.handler.SoundHandler;

public class Sound
{
	private File soundLocation;
	@Expose
	private String soundName, category;
	private SoundState state;
	private long timeLastPlayed = -1;
	private long delay;
	@Expose
	private float volume;

	public Sound(Sound sound)
	{
		this.soundLocation = sound.soundLocation;
		this.soundName = sound.soundName;
		this.category = sound.category;
		this.state = sound.state;
		this.volume = sound.volume;
	}

	public Sound(File soundLocation)
	{
		this.soundLocation = soundLocation;
		String path = soundLocation.getAbsolutePath();
		path = path.substring(0, path.lastIndexOf(File.separator));
		path = path.substring(path.lastIndexOf(File.separator) + 1);
		this.category = path;
		this.soundName = soundLocation.getName();
		this.state = SoundState.LOCAL_ONLY;
	}

	public Sound(String soundName, String category)
	{
		this.soundLocation = null;
		this.soundName = soundName;
		this.category = category;
		this.state = SoundState.REMOTE_ONLY;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getSoundName()
	{
		return soundName;
	}

	public File getSoundLocation()
	{
		return soundLocation;
	}

	public SoundState getState()
	{
		return state;
	}

	public void setTimeLastPlayed()
	{
		timeLastPlayed = System.currentTimeMillis();
	}

	public long getTimeLastPlayed()
	{
		return timeLastPlayed;
	}

	public float getVolume()
	{
		return volume;
	}

	public void setVolume(float volume)
	{
		this.volume = volume;
	}

	public void onSoundUploaded()
	{
		this.state = SoundState.SYNCED;
	}

	public void onSoundDownloaded(File soundFile)
	{
		this.soundLocation = soundFile;
		String path = soundLocation.getAbsolutePath();
		path = path.substring(0, path.lastIndexOf(File.separator));
		path = path.substring(path.lastIndexOf(File.separator) + 1);
		if(path.equals("sounds"))
			path = "";
		this.category = path;
		this.state = SoundState.SYNCED;
	}

	public boolean hasRemote()
	{
		return this.state == SoundState.SYNCED || this.state == SoundState.REMOTE_ONLY || this.state == SoundState.DOWNLOADING;
	}

	public boolean hasLocal()
	{
		return this.state == SoundState.SYNCED || this.state == SoundState.LOCAL_ONLY || this.state == SoundState.UPLOADING;
	}

	public boolean saved()
	{
		return SoundHandler.getSounds().containsKey(soundName + category);
	}

	public void setState(SoundState state)
	{
		this.state = state;
	}

	public static enum SoundState
	{
		LOCAL_ONLY, REMOTE_ONLY, SYNCED, DOWNLOADING, UPLOADING
	}
}