package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PositionMessage implements IMessage
{
	public PositionMessage() {}  // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)

	int entityID;
	double posX;
	double posY;
	double posZ;
	
	// Sending a message to the client to display particles at a specific entity's position
	public PositionMessage(int incEntityID, double x, double y, double z)
	{
		this.entityID = incEntityID;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	 
	 
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.entityID = buf.readInt();
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
	}
	 
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(entityID);
		buf.writeDouble(posX);
		buf.writeDouble(posY);
		buf.writeDouble(posZ);
	}
}
