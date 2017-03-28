package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EnderShot extends _ProjectileBase
{
	public int damage_Max; 			// How much damage we can deal, tops
	public double damage_Increase;	// By how much we increase our current damage, each tick


	public EnderShot(World world) { super(world); }

	public EnderShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// If this returns false then we won't care about gravity
	
	
	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity
		this.motionY -= 0.025;	// Default is 0.05
		
		if (this.damage < this.damage_Max) { this.damage += (double) this.damage_Increase; } // Increasing damage once per tick until we reach the max
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 6, (byte) 3);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)	// Server-side
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);	// Damage gets applied here
			target.entityHit.hurtResistantTime = 0;	// No immunity frames
            
            if (this.knockbackStrength > 0)
            {
                float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                if (f3 > 0.0F)
                {
                	target.entityHit.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3, 
                			0.1D, 
                			this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3
                	);
                }
            }
            
            this.setDead();									// Hit something, so begone.
        }    
    	else	// Hit the terrain
    	{
			// Glass breaking
    		if (Helper.tryBlockBreak(this.worldObj, this, target, 1) && this.targetsHit < 2) { this.targetsHit += 1; }
            else { this.setDead(); }	// Going straight through glass
    	}
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
	public String getEntityTexturePath() { return "textures/entity/ender.png"; }	// Our projectile texture. Don't have one, since we're using an icon
}
