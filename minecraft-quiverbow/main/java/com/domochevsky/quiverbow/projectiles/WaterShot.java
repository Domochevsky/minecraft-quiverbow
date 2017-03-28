package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.net.NetHelper;

public class WaterShot extends _ProjectileBase
{
	public WaterShot(World world) { super(world); }

	public WaterShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void doFlightSFX() 
	{
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 14, (byte) 4);
	}

	
	@Override
	public void onImpact(MovingObjectPosition target) 
	{		
		double posiX = 0;
    	double posiY = 0;
    	double posiZ = 0;
    	
    	int plusX = 0;
		int plusY = 0;
		int plusZ = 0;
    	
    	if (target.entityHit != null) // hit a entity
    	{
    		posiX = target.entityHit.posX;
    		posiY = target.entityHit.posY;
    		posiZ = target.entityHit.posZ;
    	}
    	else // hit the terrain
        {
    		posiX = target.blockX;
    		posiY = target.blockY;
    		posiZ = target.blockZ;
    		
    		if (target.sideHit == 0) { plusY = -1; } // Bottom
    		else if (target.sideHit == 1) { plusY = 1; } // Top
    		else if (target.sideHit == 2) { plusZ = -1; } // East
    		else if (target.sideHit == 3) { plusZ = 1; } // West
    		else if (target.sideHit == 4) { plusX = -1; } // North
    		else if (target.sideHit == 5) { plusX = 1; } // South
        }
    	
    	// Nether Check
		if (this.worldObj.provider.isHellWorld)
        {
			this.worldObj.playSoundEffect((double)((float)this.posX + 0.5F), (double)((float)this.posY + 0.5F), (double)((float)this.posZ + 0.5F), 
					"random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);

			NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 11, (byte) 4);
            
            return; // No water in the nether, yo
        }
		
		// Is the space free?
		if (this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.air)
    	{
			// Can we edit this block at all?
			if (this.shootingEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer) this.shootingEntity;
				if (!player.canPlayerEdit( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ, 0, null ) ) { return; }	// Nope
			}

			// Putting water there!
			this.worldObj.setBlock((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ, Blocks.flowing_water, 0, 3);
    	}
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 14, (byte) 4);
        this.worldObj.playSoundAtEntity(this, "random.splash", 1.0F, 1.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 6;	// Length, water bucket
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
}
