package com.zoonie.InteractionSounds.sound;

public class SoundInfo implements Comparable
{
	public String name, category;
	public double length;
	public long size;

	public SoundInfo(String name, String category)
	{
		this.name = name;
		this.category = category;
	}

	public SoundInfo(String name, String category, double length, long size)
	{
		this.name = name;
		this.category = category;
		this.length = length;
		this.size = size;
	}

	@Override
	public int compareTo(Object arg0)
	{
		SoundInfo other = (SoundInfo) arg0;
		int nameCompared = this.name.compareToIgnoreCase(other.name);

		if(nameCompared == 0)
		{
			return this.category.compareToIgnoreCase(other.category);
		}

		return nameCompared;
	}
}
