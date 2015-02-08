package com.zoonie.InteractionSounds.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.network.packet.server.SoundNotFoundPacket;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class RequestSoundMessage implements IMessage
{
	String soundName;
	String category;
	boolean soundExistsCheck;

	public RequestSoundMessage()
	{
	}

	public RequestSoundMessage(String soundName, String category)
	{
		this(soundName, category, false);
	}

	public RequestSoundMessage(String soundName, String category, boolean soundExistsCheck)
	{
		this.soundName = soundName;
		this.category = category;
		this.soundExistsCheck = soundExistsCheck;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int fileLength = bytes.readInt();
		char[] fileChars = new char[fileLength];
		for(int i = 0; i < fileLength; i++)
		{
			fileChars[i] = bytes.readChar();
		}
		soundName = String.valueOf(fileChars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);

		soundExistsCheck = bytes.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(soundName.length());
		for(char c : soundName.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeInt(category.length());
		for(char c : category.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeBoolean(soundExistsCheck);
	}

	public static class ServerSideHandler implements IMessageHandler<RequestSoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(RequestSoundMessage message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			Sound sound = SoundHandler.getSound(new SoundInfo(message.soundName, message.category));

			if(sound != null && !message.soundExistsCheck)
			{
				NetworkHelper.serverSoundUpload(sound, (EntityPlayerMP) player);
			}
			else if(sound == null && message.soundExistsCheck)
			{
				return new RequestSoundMessage(message.soundName, message.category);
			}
			else if(sound == null)
			{
				return new SoundNotFoundPacket(message.soundName, message.category);
			}

			return null;
		}
	}

	public static class ClientSideHandler implements IMessageHandler<RequestSoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(RequestSoundMessage message, MessageContext ctx)
		{
			Sound sound = SoundHandler.getSound(new SoundInfo(message.soundName, message.category));

			if(sound != null)
				NetworkHelper.clientSoundUpload(sound);

			return null;
		}
	}
}
