package com.zoonie.InteractionSounds.network.message;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.network.ChannelHandler;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundHandler;
import com.zoonie.InteractionSounds.sound.SoundHelper;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class ServerSoundsMessage implements IMessage
{
	private EntityPlayerMP player;
	private ArrayList<Sound> soundsList;

	public ServerSoundsMessage()
	{
	}

	public ServerSoundsMessage(EntityPlayerMP player, ArrayList<Sound> soundsList)
	{
		this.player = player;
		this.soundsList = soundsList;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		while(bytes.isReadable())
		{
			int soundNameLength = bytes.readInt();
			char[] soundNameChars = new char[soundNameLength];
			for(int j = 0; j < soundNameLength; j++)
			{
				soundNameChars[j] = bytes.readChar();
			}
			String soundName = String.valueOf(soundNameChars);

			int soundCatLength = bytes.readInt();
			char[] soundCatChars = new char[soundCatLength];
			for(int j = 0; j < soundCatLength; j++)
			{
				soundCatChars[j] = bytes.readChar();
			}
			String category = String.valueOf(soundCatChars);

			long length = bytes.readLong();
			Long size = bytes.readLong();

			SoundHandler.addRemoteSound(new SoundInfo(soundName, category, length, size));
		}
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		int i = 0;
		while(i < soundsList.size() && bytes.writerIndex() < 1000000)
		{
			Sound sound = soundsList.get(i);

			bytes.writeInt(sound.getSoundName().length());
			for(char c : sound.getSoundName().toCharArray())
			{
				bytes.writeChar(c);
			}

			bytes.writeInt(sound.getCategory().length());
			for(char c : sound.getCategory().toCharArray())
			{
				bytes.writeChar(c);
			}

			bytes.writeLong((long) SoundHelper.getSoundLength(sound.getSoundLocation()));
			bytes.writeLong(sound.getSoundLocation().length());

			i++;
		}
		if(soundsList.size() > i)
		{
			ChannelHandler.network.sendTo(new ServerSoundsMessage(player, new ArrayList<Sound>(soundsList.subList(i, soundsList.size()))), player);
		}
	}

	public static class Handler implements IMessageHandler<ServerSoundsMessage, IMessage>
	{
		@Override
		public IMessage onMessage(ServerSoundsMessage message, MessageContext ctx)
		{
			return null;
		}
	}
}
