package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class RedSpray extends _ProjectileBase
{	
	public ShotPotion pot1;
	public ShotPotion pot2;
	
	public RedSpray(World world) { super(world); }

	public RedSpray(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void doFlightSFX() 
	{
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 10, (byte) 4);
	}

	
	@Override
	public void onImpact(MovingObjectPosition movPos) 
	{
		if (movPos.entityHit != null) 		// We hit a living thing!
    	{		
			if (movPos.entityHit instanceof EntityLivingBase)	// We hit a LIVING living thing!
            {
	            EntityLivingBase entitylivingbase = (EntityLivingBase) movPos.entityHit;
	            Helper.applyPotionEffect(entitylivingbase, pot1);
	            Helper.applyPotionEffect(entitylivingbase, pot2);
            }
        }        
		// else, hit the terrain
    	
		// SFX
    	this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.5F);
    	this.worldObj.spawnParticle("redstone", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
    	
    	this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/redspray.png"; }	// Our projectile texture
}
