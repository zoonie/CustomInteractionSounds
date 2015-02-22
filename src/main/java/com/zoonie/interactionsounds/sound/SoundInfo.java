package com.zoonie.interactionsounds.sound;

public class SoundInfo implements Comparable
{
	public String name, category;
	public long length;
	public long size;

	public SoundInfo(String name, String category)
	{
		this.name = name;
		this.category = category;
	}

	public SoundInfo(String name, String category, long length, long size)
	{
		this.name = name;
		this.category = category;
		this.length = length;
		this.size = size;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((category == null) ? 0 : category.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SoundInfo other = (SoundInfo) obj;
		if(category == null)
		{
			if(other.category != null)
				return false;
		}
		else if(!category.equals(other.category))
			return false;
		if(name == null)
		{
			if(other.name != null)
				return false;
		}
		else if(!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int compareTo(Object obj)
	{
		if(obj == null)
			throw new NullPointerException();
		if(this == obj)
			return 0;

		SoundInfo other = (SoundInfo) obj;
		int nameCompared = this.name.compareToIgnoreCase(other.name);

		if(nameCompared == 0)
		{
			return this.category.compareToIgnoreCase(other.category);
		}

		return nameCompared;
	}
}
