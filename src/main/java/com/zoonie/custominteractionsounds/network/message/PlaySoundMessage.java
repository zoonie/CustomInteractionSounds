package com.zoonie.custominteractionsounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import com.zoonie.custominteractionsounds.compat.BlockPos;
import com.zoonie.custominteractionsounds.configuration.ClientConfigHandler;
import com.zoonie.custominteractionsounds.network.ChannelHandler;
import com.zoonie.custominteractionsounds.sound.SoundHandler;
import com.zoonie.custominteractionsounds.sound.SoundInfo;
import com.zoonie.custominteractionsounds.sound.SoundPlayer;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PlaySoundMessage implements IMessage
{
	String soundName, category, caller, identifier;
	int dimensionId;
	BlockPos pos;
	float volume;

	public PlaySoundMessage()
	{
	}

	public PlaySoundMessage(String name, String category, String identifier, int dimensionId, BlockPos pos, float volume, String caller)
	{
		this.soundName = name;
		this.category = category;
		this.identifier = identifier;
		this.dimensionId = dimensionId;
		this.pos = pos;
		this.volume = volume;
		this.caller = caller;
	}

	@Override
	public void fromBytes(ByteBuf bytes)
	{
		int nameLength = bytes.readInt();
		char[] nameChars = new char[nameLength];
		for(int i = 0; i < nameLength; i++)
		{
			nameChars[i] = bytes.readChar();
		}
		soundName = String.valueOf(nameChars);

		int catLength = bytes.readInt();
		char[] catChars = new char[catLength];
		for(int i = 0; i < catLength; i++)
		{
			catChars[i] = bytes.readChar();
		}
		category = String.valueOf(catChars);

		dimensionId = bytes.readInt();
		pos = new BlockPos(bytes.readInt(), bytes.readInt(), bytes.readInt());
		volume = bytes.readFloat();

		int callerLength = bytes.readInt();
		char[] callerChars = new char[callerLength];
		for(int i = 0; i < callerLength; i++)
		{
			callerChars[i] = bytes.readChar();
		}
		caller = String.valueOf(callerChars);

		int identifierLength = bytes.readInt();
		char[] idChars = new char[identifierLength];
		for(int i = 0; i < identifierLength; i++)
		{
			idChars[i] = bytes.readChar();
		}
		identifier = String.valueOf(idChars);
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

		bytes.writeInt(dimensionId);
		bytes.writeInt(pos.getX());
		bytes.writeInt(pos.getY());
		bytes.writeInt(pos.getZ());
		bytes.writeFloat(volume);

		bytes.writeInt(caller.length());
		for(char c : caller.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeInt(identifier.length());
		for(char c : identifier.toCharArray())
		{
			bytes.writeChar(c);
		}
	}

	public static class ClientSideHandler implements IMessageHandler<PlaySoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(PlaySoundMessage message, MessageContext ctx)
		{
			if(!Minecraft.getMinecraft().thePlayer.getDisplayName().equals(message.caller) && !ClientConfigHandler.muteOthers && !SoundPlayer.getInstance().isPlaying(message.identifier))
				SoundHandler.playSound(new SoundInfo(message.soundName, message.category), message.identifier, message.pos, message.volume);
			return null;
		}
	}

	public static class ServerSideHandler implements IMessageHandler<PlaySoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(PlaySoundMessage message, MessageContext ctx)
		{
			BlockPos pos = message.pos;
			TargetPoint tp = new TargetPoint(message.dimensionId, pos.getX(), pos.getY(), pos.getZ(), 16);
			ChannelHandler.network.sendToAllAround(new PlaySoundMessage(message.soundName, message.category, message.identifier, message.dimensionId, message.pos, message.volume,
					message.caller), tp);
			return null;
		}
	}
}
