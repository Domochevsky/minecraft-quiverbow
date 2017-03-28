package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class SnowShot extends _ProjectileBase
{
	public ShotPotion pot1;
	
	
	public SnowShot(World world) { super(world); }
	
	
	public SnowShot(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)	// Server-side
	{
		if (target.entityHit != null) 
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
    		target.entityHit.hurtResistantTime = 0;
            
            if (target.entityHit instanceof EntityLivingBase)
            {
	            EntityLivingBase entitylivingbase = (EntityLivingBase) target.entityHit;
	            
	            Helper.applyPotionEffect(entitylivingbase, pot1);
            }
            
            // Triple DMG vs Blazes, so applying twice more
            if (target.entityHit instanceof EntityBlaze)
            {
            	target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) (this.damage *2));
        		target.entityHit.hurtResistantTime = 0;
            }
        }
		else
		{
			// Glass breaking
    		Helper.tryBlockBreak(this.worldObj, this, target, 1);
		}

    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 12, (byte) 2);
        this.worldObj.playSoundAtEntity(this, "random.pop", 1.0F, 0.5F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public void doWaterEffect() // Called when this entity moves through water
	{ 
		// Checking for water here and turning it into ice
		int x = (int) this.posX;
		int y = (int) this.posY;
		int z = (int) this.posZ;
		
		Block hitBlock = this.worldObj.getBlock(x, y, z);
		
		if (hitBlock == Blocks.water || hitBlock == Blocks.flowing_water)
		{
			// Hit a (flowing) water block, so turning that into ice now
			this.worldObj.setBlock(x, y, z, Blocks.ice, 0, 3);
		}
	}	
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item
		type[1] = 7;	// Length, misused as item type. snowball
		type[2] = 2;	// Width
		
		return type;
	}
}
