package com.zoonie.custominteractionsounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import com.zoonie.custominteractionsounds.network.NetworkHandler;
import com.zoonie.custominteractionsounds.network.NetworkHelper;
import com.zoonie.custominteractionsounds.sound.Sound;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundInfo;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
		soundName = ByteBufUtils.readUTF8String(bytes);

		category = ByteBufUtils.readUTF8String(bytes);

		soundExistsCheck = bytes.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		ByteBufUtils.writeUTF8String(bytes, soundName);

		ByteBufUtils.writeUTF8String(bytes, category);

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
				NetworkHandler.waiting.put(new SoundInfo(message.soundName, message.category), (EntityPlayerMP) player);
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
