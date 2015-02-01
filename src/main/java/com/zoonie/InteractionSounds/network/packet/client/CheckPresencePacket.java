package com.zoonie.InteractionSounds.network.packet.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.InteractionSounds.handler.ChannelHandler;
import com.zoonie.InteractionSounds.handler.SoundHandler;
import com.zoonie.InteractionSounds.helper.NetworkHelper;
import com.zoonie.InteractionSounds.network.packet.server.SoundNotFoundPacket;
import com.zoonie.InteractionSounds.sound.Sound;
import com.zoonie.InteractionSounds.sound.SoundInfo;

public class CheckPresencePacket implements IMessage
{
	String fileName;
	String category;

	public CheckPresencePacket()
	{
	}

	public CheckPresencePacket(String soundName, String category)
	{
		this.fileName = soundName;
		this.category = category;
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
		fileName = String.valueOf(fileChars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(fileName.length());
		for(char c : fileName.toCharArray())
		{
			bytes.writeChar(c);
		}
		bytes.writeInt(category.length());
		for(char c : category.toCharArray())
		{
			bytes.writeChar(c);
		}
	}

	public static class ServerHandler implements IMessageHandler<CheckPresencePacket, IMessage>
	{
		@Override
		public IMessage onMessage(CheckPresencePacket message, MessageContext ctx)
		{
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			Sound sound = SoundHandler.getSound(new SoundInfo(message.fileName, message.category));

			if(sound != null)
			{
				NetworkHelper.serverSoundUpload(sound, (EntityPlayerMP) player);
			}
			else
			{
				NetworkHelper.sendMessageToPlayer(new SoundNotFoundPacket(message.fileName, message.category), (EntityPlayerMP) player);
			}

			return null;
		}
	}

	public static class ClientHandler implements IMessageHandler<CheckPresencePacket, IMessage>
	{
		@Override
		public IMessage onMessage(CheckPresencePacket message, MessageContext ctx)
		{
			Sound sound = SoundHandler.getSound(new SoundInfo(message.fileName, message.category));

			if(sound != null)
			{
				NetworkHelper.clientSoundUpload(sound);
			}
			else
			{
				ChannelHandler.network.sendToServer(new SoundNotFoundPacket(message.fileName, message.category));
			}
			return null;
		}
	}
}
