package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class SoulShot extends _ProjectileBase
{
	public SoulShot(World world) { super(world); }

	public SoulShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 
    	{
    		// Figuring out what we hit here now
    		if (target.entityHit instanceof EntityCreeper) { doCapture(target, 50); }
    		else if (target.entityHit instanceof EntityPigZombie) { doCapture(target, 57); }
    		else if (target.entityHit instanceof EntityCaveSpider) { doCapture(target, 59); }
    		else if (target.entityHit instanceof EntityMooshroom) { doCapture(target, 96); }
    		else if (target.entityHit instanceof EntitySkeleton) { doCapture(target, 51); }
    		else if (target.entityHit instanceof EntitySpider) { doCapture(target, 52); }
    		else if (target.entityHit instanceof EntityZombie) { doCapture(target, 54); }
    		else if (target.entityHit instanceof EntityMagmaCube) { doCapture(target, 62); }
    		else if (target.entityHit instanceof EntitySlime) { doCapture(target, 55); }
    		else if (target.entityHit instanceof EntityGhast) { doCapture(target, 56); }
    		else if (target.entityHit instanceof EntitySilverfish) { doCapture(target, 60); }
    		else if (target.entityHit instanceof EntityBlaze) { doCapture(target, 61); }
    		else if (target.entityHit instanceof EntityBat) { doCapture(target, 65); }
    		else if (target.entityHit instanceof EntityWitch) { doCapture(target, 66); }
    		else if (target.entityHit instanceof EntityPig) { doCapture(target, 90); }
    		else if (target.entityHit instanceof EntitySheep) { doCapture(target, 91); }
    		else if (target.entityHit instanceof EntityCow) { doCapture(target, 92); }
    		else if (target.entityHit instanceof EntityChicken) { doCapture(target, 93); }
    		else if (target.entityHit instanceof EntitySquid) { doCapture(target, 94); }
    		else if (target.entityHit instanceof EntityWolf) { doCapture(target, 95); }
    		else if (target.entityHit instanceof EntityOcelot) { doCapture(target, 98); }
    		else if (target.entityHit instanceof EntityHorse) { doCapture(target, 100); }
    		else if (target.entityHit instanceof EntityVillager) { doCapture(target, 120); }
    		
    		// Can't catch Arceus
    		else if (target.entityHit instanceof EntityPlayer)
    		{
    			target.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity), (float) 10);
    			this.damageShooter(); 
    		}
    		
    		else if (target.entityHit instanceof EntityDragon) { this.damageShooter(); }
    		else if (target.entityHit instanceof EntityWither) { this.damageShooter(); }
    		// else, not a known entity
    		
    		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 11, (byte) 4);
            
            this.setDead();		// We've hit something, so begone with the projectile
        }        
    	else	// Hit the terrain
    	{
    		//Block block = this.worldObj.getBlock(target.blockX, target.blockY, target.blockZ);
			
    		// Glass breaking
            if (!Helper.tryBlockBreak(this.worldObj, this, target, 1)) { this.setDead(); } // Only begone if we didn't hit glass
    	}
	}
	
	
	void doCapture(MovingObjectPosition movobj, int eggtype)
    {
    	// Make it dead and gimme the egg
    	movobj.entityHit.setDead();
		
		ItemStack egg = new ItemStack(Items.spawn_egg, 1, eggtype);
		
		if (this.shootingEntity == null) // Owner doesn't exist, so this has likely been used by a mob. Dropping the egg at target location
		{ 
			//System.out.println("[DEBUGCHEVSKY] Owner of SOUL SHOT is null. Now'd that happen?"); 
			EntityItem entityitem = new EntityItem(this.worldObj, this.posX, this.posY + 1d, this.posZ, egg);
	        entityitem.delayBeforeCanPickup = 10;
	        
	        this.worldObj.spawnEntityInWorld(entityitem);
		}
		else
		{
			EntityItem entityitem = new EntityItem(this.worldObj, this.shootingEntity.posX, this.shootingEntity.posY + 1d, this.shootingEntity.posZ, egg);
	        entityitem.delayBeforeCanPickup = 10;
	        
	        this.worldObj.spawnEntityInWorld(entityitem);
		}
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// If this returns false then we won't care about gravity
	
	
	@Override
	public void doFlightSFX()
	{
		// Doing our own (reduced) gravity
		this.motionY -= 0.025;	// Default is 0.05
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 6, (byte) 3);
	}	
	
	
	void damageShooter()
	{
		if (this.shootingEntity == null) { return; }	// Owner doesn't exist
		
		this.shootingEntity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity), (float) 10);
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 10;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/soulshot.png"; }	// Our projectile texture
}
