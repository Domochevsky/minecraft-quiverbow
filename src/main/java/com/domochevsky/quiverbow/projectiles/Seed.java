package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;

public class Seed extends _ProjectileBase
{
	public Seed(World world) { super(world); }
	
	
	public Seed(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
    		
    		target.entityHit.hurtResistantTime = 0;	// No rest for the wicked

        }        
        else 
        {
        	// Hit the terrain        	
        	int x = target.blockX;
    		int y = target.blockY;
    		int z = target.blockZ;
    		
    		Block hitBlock = this.worldObj.getBlock(x, y, z);
    		Block aboveHitBlock = this.worldObj.getBlock(x, y + 1, z);
    		
    		// Glass breaking
    		Helper.tryBlockBreak(this.worldObj, this, target, 0);
    		
    		if (hitBlock == Blocks.farmland && aboveHitBlock.getMaterial() == Material.air)
    		{
    			// Hit a farmland block and the block above is free. Planting a melon seed now
    			this.worldObj.setBlock(x, y + 1, z, Blocks.melon_stem, 0, 3);
    		}
        }
    	
        this.worldObj.playSoundAtEntity(this, "random.click", 0.2F, 3.0F);
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item icon
		type[1] = 4;	// Length, misused as item type. melon seeds
		type[2] = 2;	// Width
		
		return type;
	}
}
