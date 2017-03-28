package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class FenGoop extends _ProjectileBase
{	
	public FenGoop(World world) { super(world); }

	public FenGoop(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	public int lightTick;
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) // hit a entity
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 0);	// No dmg, but knockback
            target.entityHit.hurtResistantTime = 0;
            target.entityHit.setFire(fireDuration); 	// Some minor fire, for flavor
    	}
    	else // hit the terrain
        {        	
        	int plusX = 0;
    		int plusY = 0;
    		int plusZ = 0;
    		
    		int posiX = target.blockX;
    		int posiY = target.blockY;
    		int posiZ = target.blockZ;

    		//Block targetBlock = this.worldObj.getBlock(posiX, posiY, posiZ);
    		
    		// Is the attached block a valid material?
    		boolean canPlace = false;
    		if ( Helper.hasValidMaterial(this.worldObj, posiX, posiY, posiZ) ) { canPlace = true; }
    		
        	// Glass breaking
            if ( Helper.tryBlockBreak(this.worldObj, this, target, 0)) { canPlace = false; }
    		
    		if (target.sideHit == 0) { plusY = -1; } 		// Bottom		
    		else if (target.sideHit == 1) { plusY = 1; } 	// Top
    		else if (target.sideHit == 2) { plusZ = -1; } 	// East
    		else if (target.sideHit == 3){ plusZ = 1; } 	// West
    		else if (target.sideHit == 4){ plusX = -1; } 	// North
    		else if (target.sideHit == 5) { plusX = 1; } 	// South
    		
    		// Is the space free?
    		if (this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.air ||
    				this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.fire ||
    				this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.grass ||
    				this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.snow ||
    				this.worldObj.getBlock( (int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ).getMaterial() == Material.water)
        	{
    			// Putting light there (if we can)
    			if (canPlace)
    			{
	    			this.worldObj.setBlock(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, 0, 3);
	    			this.worldObj.setBlockMetadataWithNotify(posiX + plusX, posiY + plusY, posiZ + plusZ, target.sideHit, 3);
	    			
	    			if (this.lightTick != 0) 
	    			{ 
	    				this.worldObj.scheduleBlockUpdate(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, this.lightTick); 
	    			}
	    			// else, stays on indefinitely
    			}
    			// else, can't place. The block isn't of a valid material
        	}
    		// else, none of the allowed materials
        }
    	
    	// SFX
    	for (int i = 0; i < 8; ++i) { this.worldObj.spawnParticle("slime", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D); }
        this.worldObj.playSoundAtEntity(this, Block.soundTypeGlass.getBreakSound(), 1.0F, 1.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item
		type[1] = 5;	// Length, misused as item type. glowstone dust
		type[2] = 2;	// Width
		
		return type;
	}
}
