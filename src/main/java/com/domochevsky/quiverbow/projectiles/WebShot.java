package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class WebShot extends _ProjectileBase
{
	public WebShot(World world) { super(world); }

	public WebShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
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
		
		// Is the space free?
		if (this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.air)
    	{
			// Putting a web there!
			this.worldObj.setBlock((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ, Blocks.web, 0, 3);
    	}
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 15, (byte) 4);
        this.worldObj.playSoundAtEntity(this, "random.splash", 0.4F, 2.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 7;	// Length, snowball (misused as web ball)
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
}
