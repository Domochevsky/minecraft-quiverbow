package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

public class OWR_Shot extends _ProjectileBase
{
	public ShotPotion pot1;
	public int entitiesHit;
	public int damage_Magic;
	
	private int blocksHit;
	
	
	public OWR_Shot(World world) { super(world); }

	public OWR_Shot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void doFlightSFX()
	{				
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 7, (byte) 4);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
    		// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
			target.entityHit.hurtResistantTime = 0;	// No immunity frames
			
			target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity), (float) this.damage_Magic);
            target.entityHit.hurtResistantTime = 0;	// No immunity frames

            // Effect
            if (target.entityHit instanceof EntityLivingBase)	// We hit a LIVING living thing!
            {
            	EntityLivingBase entitylivingbase = (EntityLivingBase) target.entityHit;
            	Helper.applyPotionEffect(entitylivingbase, pot1);
            }
            
            this.setDead();
        }
    	else 	// Hit the terrain
		{
    		this.blocksHit += 1;
    		
			// Glass breaking
			if (!Helper.tryBlockBreak(this.worldObj, this, target, 3)) { this.setDead(); } // Punching through anything without restriction
			
			if (this.blocksHit > 5) { this.setDead(); }	// Put an actual limit on that
		}
		
    	// SFX
		this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 4);
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 16;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/obsidian.png"; }	// Our projectile texture
}
