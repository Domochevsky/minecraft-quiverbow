package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class Sabot_Rocket extends _ProjectileBase
{
	public float speed;
	
	public Sabot_Rocket(World world) { super(world); }

	public Sabot_Rocket(World world, Entity entity, float speed)
    {
        super(world);
        this.speed = speed;
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)	// Server-side
	{
		if (target.entityHit != null) 	// Hit a entity
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 3);
    		target.entityHit.hurtResistantTime = 0; // No immunity frames
        }
    	else	// Hit the terrain
    	{
			// Glass breaking
    		Helper.tryBlockBreak(this.worldObj, this, target, 1);
    	}

		// Spawning a rose of rockets here
		this.fireRocket(1.0f, 0.0f);
		this.fireRocket(180.0f, 0.0f);
		this.fireRocket(90.0f, 0.0f);
		this.fireRocket(-90.0f, 0.0f);
		this.fireRocket(45.0f, -45.0f);
		this.fireRocket(-45.0f, -45.0f);
		this.fireRocket(135.0f, -45.0f);
		this.fireRocket(-135.0f, 45.0f);
    	
    	// SFX
        this.worldObj.playSoundAtEntity(this, "random.break", 1.0F, 3.0F);
        NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 11, (byte) 4);
       
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	private void fireRocket(float accHor, float accVert)
	{
		SmallRocket arrow = new SmallRocket(this.worldObj, this, (this.speed / 3), accHor, accVert);	// Half speed
    	
		arrow.damage = this.damage;
    	arrow.fireDuration = this.fireDuration;
    	arrow.explosionSize = this.explosionSize;
    	arrow.shootingEntity = this.shootingEntity;
    	
    	this.worldObj.spawnEntityInWorld(arrow);
	}
	
	
	@Override
    public boolean attackEntityFrom(DamageSource source, float par2) // Big rockets can be swatted out of the way with a bit of expertise
    {
    	if (this.isEntityInvulnerable()) { return false; }
        else	// Not invulnerable
        {
            this.setBeenAttacked();

            if (source.getEntity() != null) 	// Damaged by a entity
            {
                Vec3 vec3 = source.getEntity().getLookVec();	// Which is looking that way...

                if (vec3 != null) 
                {
                    this.motionX = vec3.xCoord;
                    this.motionY = vec3.yCoord;
                    this.motionZ = vec3.zCoord;
                }

                if (source.getEntity() instanceof EntityLivingBase) { this.shootingEntity = (EntityLivingBase)source.getEntity(); }

                return true;
            }
            // else, not damaged by an entity
    	}
    	
    	return false;
    }
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, projectile
		type[1] = 10;	// Length
		type[2] = 3;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/rocketsabot.png"; }
}
