package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class SunLight extends _ProjectileBase implements IEntityAdditionalSpawnData
{
	public int travelTicksMax;
	public int targetsHitMax;
	
	public SunLight(World world) { super(world); }

	public SunLight(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + entity.getEyeHeight();
        this.ownerZ = entity.posZ;
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// Affected by gravity?

	
	@Override
	public void doFlightSFX()
	{
		if (this.ticksExisted > this.ticksInAirMax) { this.setDead(); }	// There's only so long we can exist
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 4, (byte) 1);
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 1, (byte) 1);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
            target.entityHit.hurtResistantTime = 0;	// No immunity frames
            
            // Fire
            target.entityHit.setFire(fireDuration);
        }
		else 
        { 
        	Block block = this.worldObj.getBlock(target.blockX, target.blockY, target.blockZ);
			
			// Glass breaking
        	Helper.tryBlockBreak(this.worldObj, this, target, 2);	// Strong
            
        	// Let's create fire here
        	if (block != Blocks.fire)
        	{
        		if (this.worldObj.getBlock(target.blockX, target.blockY + 1, target.blockZ).isAir(this.worldObj, target.blockX, target.blockY + 1, target.blockZ))
	        	{
	        		// the block above the block we hit is air, so let's set it on fire!
	        		this.worldObj.setBlock(target.blockX, target.blockY + 1, target.blockZ, Blocks.fire, 0, 3);
	        	}
        		// else, not an airblock above this
        	}
        	
        	// Have we hit snow? Turning that into snow layer
        	else if (block == Blocks.snow)
        	{
        		this.worldObj.setBlock(target.blockX, target.blockY, target.blockZ, Blocks.snow_layer, 7, 3);
        	}
        	
        	// Have we hit snow layer? Melting that down into nothing
        	else if (block == Blocks.snow_layer)
        	{
        		int currentMeta = this.worldObj.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
        		// Is this taller than 0? Melting it down then
        		if (currentMeta > 0) { this.worldObj.setBlock(target.blockX, target.blockY, target.blockZ, Blocks.snow_layer, currentMeta - 1, 3); }
        		// Is this 0 already? Turning it into air
        		else { this.worldObj.setBlockToAir(target.blockX, target.blockY, target.blockZ); }
        	}
        	
        	// Have we hit ice? Turning that into water
        	else if (block == Blocks.ice)
        	{
        		this.worldObj.setBlock(target.blockX, target.blockY, target.blockZ, Blocks.water, 0, 3);
        	}
        	
        	// Have we hit (flowing) water? Evaporating that
        	else if (block == Blocks.water || block == Blocks.flowing_water)
        	{
        		this.worldObj.setBlockToAir(target.blockX, target.blockY, target.blockZ);
        	}
        }
		
		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 4, (byte) 2);
		this.worldObj.playSoundAtEntity(this, "fireworks.largeBlast", 0.7F, 1.5F);
		
		// Going through terrain until our time runs out, but don't damage anything
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 7;	// Type 7, beam weapon (Sunray)
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/rod.png"; }	// Our projectile texture
	
	
	@Override
	public void writeSpawnData(ByteBuf buffer) 			// save extra data on the server
    {
		buffer.writeDouble(this.ownerX);
		buffer.writeDouble(this.ownerY);
		buffer.writeDouble(this.ownerZ);
    }
    
	@Override
	public void readSpawnData(ByteBuf additionalData) 	// read it on the client
	{ 
		this.ownerX = additionalData.readDouble();
		this.ownerY = additionalData.readDouble();
		this.ownerZ = additionalData.readDouble();
	}
}
