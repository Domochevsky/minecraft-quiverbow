package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class OSP_Shot extends _ProjectileBase
{
	public ShotPotion pot1;
	public int entitiesHit;	
	
	public OSP_Shot(World world) { super(world); }

	public OSP_Shot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity
				
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 1);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
            target.entityHit.hurtResistantTime = 0;	// No immunity frames
            
            if (target.entityHit instanceof EntityLivingBase)	// We hit a LIVING living thing!
            {
	            EntityLivingBase entitylivingbase = (EntityLivingBase) target.entityHit;
	            
	            // Effect ID, duration, amplifier, is ambient
	            Helper.applyPotionEffect(entitylivingbase, pot1);
            }
            // else, not sufficiently living 
            
            this.setDead();	// Hit a entity, so begone.
        }
		else	// Hit the terrain
    	{
			// Glass breaking, 1 layer
    		if (Helper.tryBlockBreak(this.worldObj, this, target, 1) && this.targetsHit < 1) { this.targetsHit += 1; }
            else { this.setDead(); }	// Punching through glass
    	}
		
		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 9, (byte) 2);
		this.worldObj.playSoundAtEntity(this, "random.bowhit", 0.4F, 0.5F);
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 4;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/obsidian.png"; }	// Our projectile texture
}
