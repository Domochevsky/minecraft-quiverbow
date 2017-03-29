package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class ParticleMessage implements IMessage
{
	int entityID;
	byte particleType;
	byte strength;
	
	public ParticleMessage() {}  // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)

	
	// Sending a message to the client to display particles at a specific entity's position
	public ParticleMessage(int incEntityID, byte type, byte strength)
	{
		this.entityID = incEntityID;
		this.particleType = type;
		this.strength = strength;
	}
	 
	 
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.entityID = buf.readInt();
		this.particleType = buf.readByte();
		this.strength = buf.readByte();
	}
	 
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityID);
		buf.writeByte(particleType);
		buf.writeByte(strength);
	}
}
