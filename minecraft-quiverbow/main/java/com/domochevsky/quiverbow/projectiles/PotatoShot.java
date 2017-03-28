package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class PotatoShot extends _ProjectileBase
{
	private boolean shouldDrop;
	
	
	public PotatoShot(World world) { super(world); }

	
	public PotatoShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	public void setDrop(boolean set) { this.shouldDrop = set; }
	
	
	@Override
	public void onImpact(MovingObjectPosition target) 
	{
		if (target.entityHit != null) 
    	{
    		// Damage
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);
        }
        else 
        {        	
        	// Glass breaking
            Helper.tryBlockBreak(this.worldObj, this, target, 1);
            
        	if (this.shouldDrop && this.canBePickedUp)	// If we can be picked up then we're dropping now
        	{
	        	ItemStack nuggetStack = new ItemStack(Items.baked_potato);
	        	EntityItem entityitem = new EntityItem(this.worldObj, target.blockX, target.blockY + 0.5d, target.blockZ, nuggetStack);
	            entityitem.delayBeforeCanPickup = 10;
	            
	            if (captureDrops) { capturedDrops.add(entityitem); }
	            else { this.worldObj.spawnEntityInWorld(entityitem); }
        	}
        }
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 2);
        this.worldObj.playSoundAtEntity(this, "random.eat", 0.6F, 0.7F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 3;	// Length, misused for icon type. 3 = cooked potato
		type[2] = 2;	// Width, not used
		
		return type;
	}
}
