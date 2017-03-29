package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class Thorn extends _ProjectileBase
{
	public Thorn(World world) { super(world); }

	public Thorn(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }

	
	public Thorn(World world, Entity entity, float speed, float yaw, float pitch) 
    {
        super(world);
        this.doSetup(entity, speed, 0, 0, yaw, pitch);
    }
	

	@Override
	public void onImpact(MovingObjectPosition movPos)	// Server-side
	{
		if (movPos.entityHit != null) 
    	{
    		movPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
    		movPos.entityHit.hurtResistantTime = 0;	// No rest for the wicked
        }
    	else	// Hit the terrain
    	{
    		Helper.tryBlockBreak(this.worldObj, this, movPos, 1);
    	}
		
		// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 13, (byte) 1);
		
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
	public String getEntityTexturePath() { return "textures/entity/thorn.png"; }	// Our projectile texture
}
