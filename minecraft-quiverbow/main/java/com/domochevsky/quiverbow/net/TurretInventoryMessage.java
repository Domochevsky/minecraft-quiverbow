package com.domochevsky.quiverbow.net;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class TurretInventoryMessage implements IMessage
{
	public TurretInventoryMessage() {}  // this constructor is required otherwise you'll get errors (used somewhere in fml through reflection)

	int entityID;
	int itemID;
	int itemSlot;
	int metadata;
	
	
	// Sending a message to the client to inform them about turret state changes
	public TurretInventoryMessage(int incEntityID, int itemID, int slot, int metadata)
	{
		this.entityID = incEntityID;
		this.itemID = itemID;
		this.itemSlot = slot;
		this.metadata = metadata;
	}
	 
	 
	@Override
	public void fromBytes(ByteBuf buf)
	{
		// the order is important
		this.entityID = buf.readInt();
		this.itemID = buf.readInt();
		this.itemSlot = buf.readInt();
		this.metadata = buf.readInt();
	}
	 
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityID);
		buf.writeInt(this.itemID);
		buf.writeInt(this.itemSlot);
		buf.writeInt(this.metadata);
	}
}
