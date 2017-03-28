package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.net.NetHelper;

public class BigRocket extends _ProjectileBase
{
	public int travelTicksMax;
	public boolean dmgTerrain;
	
	public BigRocket(World world) { super(world); }

	public BigRocket(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)	// Server-side
	{
		boolean griefing = true;	// Allowed by default
		
		if (this.shootingEntity instanceof EntityPlayer)
		{
			griefing = this.dmgTerrain;	// It's up to player settings to allow/forbid this
		}
		else
		{
			griefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");	// Are we allowed to break things?
		}
		
		this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionSize, griefing);	// Bewm
		
		this.setDead(); 	// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public void doFlightSFX() 
	{ 
		if (travelTicksMax > 0)	// We have a fixed travel time, so lesse...
		{
			if (this.ticksExisted > this.travelTicksMax) // Our fuse has run out
			{
				boolean griefing = true;	// Allowed by default
				
				if (this.shootingEntity instanceof EntityPlayer)
				{
					griefing = this.dmgTerrain;	// It's up to player settings to allow/forbid this
				}
				else
				{
					griefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");	// Are we allowed to break things?
				}
				
				this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionSize, griefing);	// Bewm
				
				this.setDead(); 	// We've hit something, so begone with the projectile
			}
		}
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 2, (byte) 8);
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
	public String getEntityTexturePath() { return "textures/entity/rocket.png"; }
}
