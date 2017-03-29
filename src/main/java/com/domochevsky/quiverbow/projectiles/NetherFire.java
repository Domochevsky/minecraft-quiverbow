package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class NetherFire extends _ProjectileBase
{	
	public NetherFire(World world) { super(world); }

	public NetherFire(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 4, (byte) 2);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
			target.entityHit.hurtResistantTime = 0;
			
			// Effect
			target.entityHit.setFire(this.fireDuration);
			
        }        
        else 
        { 
        	Block block = this.worldObj.getBlock(target.blockX, target.blockY, target.blockZ);
			
			// Glass breaking
        	Helper.tryBlockBreak(this.worldObj, this, target, 1);	// Medium
            
        	// Let's create fire here (if we're allowed to)
        	if (this.worldObj.getGameRules().getGameRuleBooleanValue("doFireTick") && block != Blocks.fire)
        	{
        		if (this.worldObj.getBlock(target.blockX, target.blockY + 1, target.blockZ).isAir(this.worldObj, target.blockX, target.blockY + 1, target.blockZ))
	        	{
	        		// the block above the block we hit is air, so let's set it on fire!
	        		this.worldObj.setBlock(target.blockX, target.blockY + 1, target.blockZ, Blocks.fire, 0, 3);
	        	}
        		// else, not a airblock above this
        	}
        	
        	// Have we hit snow? Turning that into snow layer
        	if (block == Blocks.snow)
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
        	
        	Block topBlock = this.worldObj.getBlock(target.blockX, target.blockY + 1, target.blockZ);
        	
        	// Did we hit grass? Burning it
        	if (topBlock.getMaterial() == Material.plants)
        	{
        		this.worldObj.setBlock(target.blockX, target.blockY + 1, target.blockZ, Blocks.fire, 0, 3);
        	}
        	if (block.getMaterial() == Material.plants)
        	{
        		this.worldObj.setBlock(target.blockX, target.blockY, target.blockZ, Blocks.fire, 0, 3);
        	}
        }
    	
		// SFX
    	this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.5F);
    	this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);	
    	
        this.setDead();		// We've hit something, so begone with the projectile. hitting glass only once
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/netherspray.png"; }	// Our projectile texture
}
