package com.zoonie.interactionsounds.sound;

import net.minecraft.util.BlockPos;

public class SoundPlayInfo
{
	public String identifier;
	public BlockPos pos;
	public float volume;
	public String loop;

	public SoundPlayInfo(String identifier, BlockPos pos, float volume, String loop)
	{
		this.identifier = identifier;
		this.pos = pos;
		this.volume = volume;
		this.loop = loop;
	}
}
