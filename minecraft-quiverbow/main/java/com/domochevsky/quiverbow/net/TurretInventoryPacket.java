package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.Helper_Client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class TurretInventoryPacket implements IMessageHandler<TurretInventoryMessage, IMessage>
{
	@Override
	public IMessage onMessage(TurretInventoryMessage message, MessageContext ctx)
	{		
		if (ctx.side.isClient())	// just to make sure that the side is correct 
		{
			Helper_Client.setTurretInventory(message.entityID, message.itemID, message.itemSlot, message.metadata);
		}
		
		return null;	// Don't care about returning anything
	}
}
