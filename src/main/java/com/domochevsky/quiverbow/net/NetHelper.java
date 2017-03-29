package com.domochevsky.quiverbow.net;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;

import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class NetHelper 
{    
	// Same as below, but sends it to all players
	public static void sendParticleMessageToAllPlayers(World world, int entityID, byte particle, byte strength)
	{
		if (world.isRemote) { return; }	// Server-use only
		List players = world.playerEntities;
		
		if (players.isEmpty()) { return; }	// No players? Who called this?
		
		int counter = 0;
		while (counter < players.size())
		{
			sendParticleMessage((Entity) players.get(counter), entityID, particle, strength);
			counter += 1;
		}
	}
	
	
	// Sends a custom packet to the other side
	public static void sendParticleMessage(Entity user, int entityID, byte particle, byte strength)
	{
		if (user instanceof EntityPlayerMP)
		{			
			IMessage msg = new ParticleMessage(entityID, particle, strength);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP)user);
		}
		// else, not a player we're trying to send this to
	}	
	
	
	// Same as below, but sends it to all players
	public static void sendPositionMessageToAllPlayers(World world, int entityID, double x, double y, double z)
	{
		if (world.isRemote) { return; }	// Server-use only
		List players = world.playerEntities;
		
		if (players.isEmpty()) { return; }	// No players? Who called this? D:
		
		int counter = 0;
		while (counter < players.size())
		{
			sendPositionMessage((Entity) players.get(counter), entityID, x, y, z);
			counter += 1;
		}
	}
	
	
	// Same as above, but tries to save bandwidth by only sending packets to players who actually have a chance to see this event
	public static void sendPositionMessageToPlayersInRange(World world, Entity entity, double x, double y, double z)
	{
		if (world.isRemote) { return; }	// Server-use only
		AxisAlignedBB box = entity.boundingBox.expand(64, 64, 64);	
		List list = world.getEntitiesWithinAABBExcludingEntity(entity, box);
		
		int counter = 0;
		
		while (counter < list.size())
		{
			Entity potentialPlayer = (Entity) list.get(counter);
			
			if (potentialPlayer instanceof EntityPlayer)
			{
				sendPositionMessage(potentialPlayer, entity.getEntityId(), x, y, z);
			}
			counter += 1;
		}
	}
	
	
	// Sends a custom packet to the client
	private static void sendPositionMessage(Entity user, int entityID, double x, double y, double z)
	{
		if (user instanceof EntityPlayerMP)
		{
			IMessage msg = new PositionMessage(entityID, x, y, z);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP)user);
		}
		// else, not a player we're trying to send this to
	}
	
	
	// Informing the player that they just got knocked back by their weapon
	public static void sendKickbackMessage(Entity user, byte strength)
	{
		if (user instanceof EntityPlayerMP)
		{
			IMessage msg = new KickbackMessage(strength);
			PacketHandler.net.sendTo(msg, (EntityPlayerMP) user);
		}
	}
	
	
	public static void sendTurretStateMessageToPlayersInRange(World world, Entity_AA turret, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade, boolean hasPlatingUpgrade, boolean hasComUpgrade)
	{
		// Step 1, who's in range?
		if (world.isRemote) { return; }	// Server-use only
		
		AxisAlignedBB box = turret.boundingBox.expand(64, 64, 64);	
		List list = world.getEntitiesWithinAABBExcludingEntity(turret, box);
		
		int counter = 0;
		
		while (counter < list.size())
		{
			Entity potentialPlayer = (Entity) list.get(counter);
			
			if (potentialPlayer instanceof EntityPlayer)
			{
				sendTurretStateMessageToPlayer((EntityPlayer) potentialPlayer, turret.getEntityId(), hasArmor, hasWeaponUpgrade, hasRidingUpgrade, hasPlatingUpgrade, hasComUpgrade);
			}
			// else, not a player. Don't care
			
			counter += 1;
		}
	}
	
	
	public static void sendTurretInventoryMessageToPlayersInRange(World world, Entity_AA turret, int itemID, int itemSlot, int metadata)
	{
		// Step 1, who's in range?
		if (world.isRemote) { return; }	// Server-use only
		
		AxisAlignedBB box = turret.boundingBox.expand(64, 64, 64);	
		List list = world.getEntitiesWithinAABBExcludingEntity(turret, box);
		
		int counter = 0;
		
		while (counter < list.size())
		{
			Entity potentialPlayer = (Entity) list.get(counter);
			
			if (potentialPlayer instanceof EntityPlayer)
			{
				sendTurretInventoryMessageToPlayer((EntityPlayer) potentialPlayer, turret.getEntityId(), itemID, itemSlot, metadata);
			}
			// else, not a player. Don't care
			
			counter += 1;
		}
	}
	
	
	// Informing the client about a state change
	private static void sendTurretStateMessageToPlayer(EntityPlayer player, int turretEntityID, boolean hasArmor, boolean hasWeaponUpgrade, boolean hasRidingUpgrade, boolean hasPlatingUpgrade, boolean hasComUpgrade)
	{
		IMessage msg = new TurretStateMessage(turretEntityID, hasArmor, hasWeaponUpgrade, hasRidingUpgrade, hasPlatingUpgrade, hasComUpgrade);
		PacketHandler.net.sendTo(msg, (EntityPlayerMP)player);
	}
	
	
	private static void sendTurretInventoryMessageToPlayer(EntityPlayer player, int turretEntityID, int itemID, int itemSlot, int metadata)
	{
		IMessage msg = new TurretInventoryMessage(turretEntityID, itemID, itemSlot, metadata);
		PacketHandler.net.sendTo(msg, (EntityPlayerMP)player);
	}
}
