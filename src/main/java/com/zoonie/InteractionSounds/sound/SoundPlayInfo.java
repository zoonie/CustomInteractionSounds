package com.zoonie.InteractionSounds.sound;

public class SoundPlayInfo
{
	public String identifier;
	public int x, y, z;
	public float volume;

	public SoundPlayInfo(String identifier, int x, int y, int z, float volume) {
		this.identifier = identifier;
		this.x = x;
		this.y = y;
		this.z = z;
		this.volume = volume;
	}
}
