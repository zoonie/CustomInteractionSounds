package com.zoonie.custominteractionsounds.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zoonie.custominteractionsounds.network.ChannelHandler;
import com.zoonie.custominteractionsounds.sound.SoundPlayer;

public class RepeatSoundMessage implements IMessage
{
	String identifier, caller;
	int dimensionId;
	int x, y, z;

	public RepeatSoundMessage()
	{
	}

	public RepeatSoundMessage(String identifier, int dimensionId, int x, int y, int z, String caller)
	{
		this.identifier = identifier;
		this.dimensionId = dimensionId;
		this.x = x;
		this.y = y;
		this.z = z;
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
		identifier = String.valueOf(nameChars);

		dimensionId = bytes.readInt();
		x = bytes.readInt();
		y = bytes.readInt();
		z = bytes.readInt();

		int callerLength = bytes.readInt();
		char[] callerChars = new char[callerLength];
		for(int i = 0; i < callerLength; i++)
		{
			callerChars[i] = bytes.readChar();
		}
		caller = String.valueOf(callerChars);
	}

	@Override
	public void toBytes(ByteBuf bytes)
	{
		bytes.writeInt(identifier.length());
		for(char c : identifier.toCharArray())
		{
			bytes.writeChar(c);
		}

		bytes.writeInt(dimensionId);
		bytes.writeInt(x);
		bytes.writeInt(y);
		bytes.writeInt(z);

		bytes.writeInt(caller.length());
		for(char c : caller.toCharArray())
		{
			bytes.writeChar(c);
		}
	}

	public static class ClientSideHandler implements IMessageHandler<RepeatSoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(RepeatSoundMessage message, MessageContext ctx)
		{
			if(!Minecraft.getMinecraft().thePlayer.getDisplayNameString().equals(message.caller))
				SoundPlayer.getInstance().playSound(message.identifier, message.x, message.y, message.z);
			return null;
		}
	}

	public static class ServerSideHandler implements IMessageHandler<RepeatSoundMessage, IMessage>
	{
		@Override
		public IMessage onMessage(RepeatSoundMessage message, MessageContext ctx)
		{
			TargetPoint tp = new TargetPoint(message.dimensionId, message.x, message.y, message.z, 16);
			ChannelHandler.network.sendToAllAround(new RepeatSoundMessage(message.identifier, message.dimensionId, message.x, message.y, message.z, message.caller), tp);
			return null;
		}
	}
}
