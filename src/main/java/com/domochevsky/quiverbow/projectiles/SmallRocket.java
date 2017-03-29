package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class SmallRocket extends _ProjectileBase
{
	public boolean dmgTerrain;


	public SmallRocket(World world) { super(world); }

	public SmallRocket(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	public SmallRocket(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)	// Server-side
	{
		if (target.entityHit != null) 	// Hit a entity
    	{
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
			target.entityHit.hurtResistantTime = 0; // No immunity frames
			
			// Knockback
            double f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (f3 > 0.0F) 
            {
            	target.entityHit.addVelocity(
        			this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3, 
        			0.1D, 
        			this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f3
            	);
            }
            
			// Effect
			if ( !(target.entityHit instanceof EntityEnderman) ) { target.entityHit.setFire(this.fireDuration); } 
			
			this.setDead();	
			
		}
		else 	// Hit a block
		{ 
			Block block = this.worldObj.getBlock(target.blockX, target.blockY, target.blockZ);
			
			// Glass breaking, once
			if (Helper.tryBlockBreak(this.worldObj, this, target, 1) && this.targetsHit < 1) { this.targetsHit += 1; }
			else { this.setDead(); }	// else, either we didn't break that block or we already hit one entity
            
			// Let's ignite TNT explicitly here.
			if (block == Blocks.tnt)
			{
				this.worldObj.setBlockToAir(target.blockX, target.blockY, target.blockZ); // setBlockToAir
               
				EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(this.worldObj, 
                		(double)((float)target.blockX + 0.5F), 
                		(double)((float)target.blockY + 0.5F), 
                		(double)((float)target.blockZ + 0.5F), 
                		this.shootingEntity);									

                this.worldObj.spawnEntityInWorld(entitytntprimed);			// This is TNT, so begone with that block and replace it with primed TNT
                
                this.worldObj.playSoundAtEntity(entitytntprimed, "random.fuse", 1.0F, 1.0F);
			}
			// else, block is not TNT
		} 
		
		if (!this.isInWater()) // Only explode if we're not in water
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
			
			this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, (float) this.explosionSize, griefing); 	
			// 4.0F is TNT, false is for "not flaming"
			// Editchevsky: Actually, false is double-used for "don't damage terrain"
		}
	}
	
	
	@Override
	public void doFlightSFX() 
	{ 
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 2, (byte) 4);
	}
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, projectile
		type[1] = 8;	// Length
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/rocket.png"; }
}
