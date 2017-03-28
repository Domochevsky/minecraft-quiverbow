package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.Helper_Client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class TurretStatePacket implements IMessageHandler<TurretStateMessage, IMessage>
{
	@Override
	public IMessage onMessage(TurretStateMessage message, MessageContext ctx)
	{		
		if (ctx.side.isClient())	// just to make sure that the side is correct 
		{
			Helper_Client.setTurretState(message.entityID,
					message.hasArmorUpgrade, 
					message.hasWeaponUpgrade, 
					message.hasRidingUpgrade, 
					message.hasPlatingUpgrade, 
					message.hasCommunicationUpgrade);
		}
		
		return null;	// Don't care about returning anything
	}
}
