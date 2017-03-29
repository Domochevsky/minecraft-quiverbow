package com.domochevsky.quiverbow.net;

import com.domochevsky.quiverbow.Helper_Client;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ParticlePacket implements IMessageHandler<ParticleMessage, IMessage>
{
	@Override
	public IMessage onMessage(ParticleMessage message, MessageContext ctx)
	{		
		if (ctx.side.isClient())	// just to make sure that the side is correct 
		{
			Helper_Client.displayParticles(message.entityID, message.particleType, message.strength);
		}
		
		return null;	// Don't care about returning anything
	}
}