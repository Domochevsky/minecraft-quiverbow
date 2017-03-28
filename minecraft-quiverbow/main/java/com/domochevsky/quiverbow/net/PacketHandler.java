package com.domochevsky.quiverbow.net;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler 
{
	public static SimpleNetworkWrapper net;
	
	public static void initPackets()
	{
		net = NetworkRegistry.INSTANCE.newSimpleChannel("quiverchevsky".toUpperCase());
		 
		registerMessage(ParticlePacket.class, ParticleMessage.class);
		registerMessage(PositionPacket.class, PositionMessage.class);
		registerMessage(KickbackPacket.class, KickbackMessage.class);
		registerMessage(TurretInventoryPacket.class, TurretInventoryMessage.class);
		registerMessage(TurretStatePacket.class, TurretStateMessage.class);
	}
	 
	 
	 private static int nextPacketId = 0;
	
	 
	 private static void registerMessage(Class packet, Class message)
	 {
		 net.registerMessage(packet, message, nextPacketId, Side.CLIENT);	// Only care about sending things to the client
		 //net.registerMessage(packet, message, nextPacketId, Side.SERVER);
		 nextPacketId++;
	 }
}
