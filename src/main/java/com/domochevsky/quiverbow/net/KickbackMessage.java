package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class KickbackMessage implements IMessage
{
	public KickbackMessage() {}  // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)

	byte strength;
	
	// Sending a message to the client to display particles at a specific entity's position
	public KickbackMessage(byte str)
	{
		this.strength = str;
	}
	 
	 
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.strength = buf.readByte();
	}
	 
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(strength);
	}
}
