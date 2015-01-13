package com.zoonie.InteractionSounds.EventHandlers;

import com.zoonie.InteractionSounds.sound.Sound;

public class Interaction
{
	private int mouseButton;
	private String item;
	private String target;
	private Sound sound;
	private long timeFired;
	private long delay;
	
	public Interaction(int mouseButton, String item, String target)
	{
		this.mouseButton = mouseButton;
		this.item = item;
		this.target = target;
		this.timeFired = System.currentTimeMillis();
	}
	
	public void setSound(Sound sound)
	{
		this.sound = sound;
	}
	
	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	public int getMouseButton()
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
		result = prime * result + mouseButton;
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interaction other = (Interaction) obj;
		if (item == null)
		{
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (mouseButton != other.mouseButton)
			return false;
		if (target == null)
		{
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
}
