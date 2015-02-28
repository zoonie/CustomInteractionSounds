package com.zoonie.custominteractionsounds.interaction;

import static com.zoonie.custominteractionsounds.language.LanguageHelper.translate;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class Interaction implements Serializable, Comparable
{
	@Expose
	private String mouseButton, item, target, generalTargetName;
	@Expose
	private boolean isEntity = false;

	public Interaction(String mouseButton, String item, String target)
	{
		this(mouseButton, item, target, target);
	}

	public Interaction(String mouseButton, String item, String target, String generalTargetName)
	{
		this.mouseButton = mouseButton;
		this.item = item;
		this.target = target;
		this.generalTargetName = generalTargetName;
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

	public String getGeneralTargetName()
	{
		return generalTargetName;
	}

	public void setItem(String item)
	{
		this.item = item;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void useGeneralTargetName()
	{
		target = generalTargetName;
	}

	public void setIsEntity(boolean isEntity)
	{
		this.isEntity = isEntity;
	}

	public boolean isEntity()
	{
		return isEntity;
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

	@Override
	public int compareTo(Object obj)
	{
		if(obj == null)
			throw new NullPointerException();
		if(this == obj)
			return 0;

		Interaction other = (Interaction) obj;

		int comparison = this.mouseButton.compareToIgnoreCase(other.mouseButton);
		if(comparison == 0)
		{
			comparison = translate(this.target).compareToIgnoreCase(translate(other.target));
			if(comparison == 0)
			{
				comparison = translate(this.item).compareToIgnoreCase(translate(other.item));
			}
		}
		return comparison;
	}

}
