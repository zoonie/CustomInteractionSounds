package com.zoonie.InteractionSounds.EventHandlers;

import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;

public class Interaction
{
	private String mouseButton;
	private String item;
	private String target;
	private Sound sound;
	private long timeLastPlayed;
	private long delay;

	public Interaction(String mouseButton, String item, String target) {
		this(mouseButton, item, target, null);
	}

	public Interaction(String mouseButton, String item, String target, String soundName) {
		this.mouseButton = mouseButton;
		this.item = item;
		this.target = target;
		this.timeLastPlayed = -1;
		this.sound = SoundHandler.getSoundByName(soundName);
	}

	public void setSound(Sound sound)
	{
		this.sound = sound;
	}

	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	public String getMouseButton()
	{
		return mouseButton;
	}

	public String getItem()
	{
		return item;
	}

	public String getTarget()
	{
		return target;
	}

	public long getTimeLastPlayed()
	{
		return timeLastPlayed;
	}

	public void setTimeLastPlayed()
	{
		this.timeLastPlayed = System.currentTimeMillis();
	}

	public Sound getSound()
	{
		return sound;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + ((mouseButton == null) ? 0 : mouseButton.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Interaction other = (Interaction) obj;
		if(item == null)
		{
			if(other.item != null)
				return false;
		}
		else if(!item.equals(other.item))
			return false;
		if(mouseButton == null)
		{
			if(other.mouseButton != null)
				return false;
		}
		else if(!mouseButton.equals(other.mouseButton))
			return false;
		if(target == null)
		{
			if(other.target != null)
				return false;
		}
		else if(!target.equals(other.target))
			return false;
		return true;
	}

}
