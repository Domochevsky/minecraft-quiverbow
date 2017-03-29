package com.domochevsky.quiverbow.AI;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.*;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.weapons.ERA;
import com.domochevsky.quiverbow.weapons.EnderRifle;
import com.domochevsky.quiverbow.weapons.Endernymous;
import com.domochevsky.quiverbow.weapons.FrostLancer;
import com.domochevsky.quiverbow.weapons.LightningRed;
import com.domochevsky.quiverbow.weapons.MediGun;
import com.domochevsky.quiverbow.weapons.RPG;
import com.domochevsky.quiverbow.weapons.RPG_Imp;
import com.domochevsky.quiverbow.weapons.Sunray;
import com.domochevsky.quiverbow.weapons._WeaponBase;

public class AI_Targeting
{
	public static void lookAtTarget(Entity_AA turret, boolean secondRail)	// Server side
	{
		lookAtTarget(turret, turret.currentTarget, secondRail);
	}
	
	public static void lookAtTarget(Entity_AA turret, Entity target, boolean secondRail)	// Server side
	{
		if (target == null) { return; }	// Nobody to look at there
		
		turret.faceTarget(target, 30.0F, 30.0F);
		
		adjustAim(turret, secondRail, target.posX, target.posY, target.posZ);
	}
	
	
	public static void adjustAim(Entity_AA turret, boolean secondRail, double posX, double posY, double posZ)
	{
		_WeaponBase currentWeapon = turret.firstWeapon;
		if (secondRail) { currentWeapon = turret.secondWeapon; }
		
		if (currentWeapon == null) { return; }	// Has no weapon, so no point in adjusting anything
		
		if (currentWeapon instanceof LightningRed) { }	// Laser
		else if (currentWeapon instanceof MediGun) { }
		else if (currentWeapon instanceof Sunray) { }
		else if (currentWeapon instanceof ERA) { }
		else if (currentWeapon instanceof Endernymous) { }
		else
		{
			double distance = getDistanceSqToTarget(turret, posX, posY, posZ);
			double angleMod = (0.045 * distance) / (currentWeapon.Speed * currentWeapon.Speed);	// 0.05
			
			if (currentWeapon instanceof FrostLancer) { angleMod /= 2; }		// Half gravity
			else if (currentWeapon instanceof EnderRifle) { angleMod /= 2; }	// Half gravity
			
			turret.rotationPitch -= MathHelper.wrapAngleTo180_double(angleMod);
		}
	}
	
	
	private static double getDistanceSqToTarget(Entity observer, double posX, double posY, double posZ)
	{
		double distanceX = observer.posX - posX;
		double distanceY = observer.posY - posY;
		double distanceZ = observer.posZ - posZ;
		
		return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
	}
	
	
	// Just looking
	public static void targetNearestEntity(Entity_AA turret)
	{
		if (turret.hasRidingUpgrade && turret.riddenByEntity != null) // Not targeting anyone while being ridden
		{
			turret.currentTarget = null;
			return;
		}
		
		if (turret.targetDelay > 0)
		{
			turret.targetDelay -= 1;	// Still ticking down
			return;
		}
		
		Entity previousTarget = turret.currentTarget;
		
		// Having a gander
		turret.targetDelay = 20;	// Reset for next tick
		
		AxisAlignedBB box = turret.boundingBox.expand(turret.attackDistance, turret.attackDistance, turret.attackDistance);
		
		List list = turret.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
		
		int counter = 0;
		
		Entity closestEntity = null;
		Entity potentialEntity;
		
		double closestDistance = 10000000;
		
		boolean skip = false;
		
		while (counter < list.size())
		{
			skip = false; // Reset
			potentialEntity = (Entity) list.get(counter);
			
			if (isNameOnWhitelist(turret, Commands.cmdTargetFriendly))	// Targeting the owner (and whitelisted entities)
			{
				if (!isEntityOnInternalWhitelist(turret, potentialEntity) && !isEntityListed(turret, potentialEntity)) { skip = true; }	// Not whitelisted, so not healing them
				else if (!(potentialEntity instanceof EntityLiving)) { skip = true; }		// Not a living thing, so not healing them
				else
				{
					EntityLiving living = (EntityLiving) potentialEntity;
					
					if (isNameOnWhitelist(turret, Commands.cmdInjuredOnly) && living.getHealth() >= living.getMaxHealth())	// Already at max health
					{
						skip = true;
					}
					// Either don't care about injury or they're below max health. Checks out!
				}
			}
			else if (isNameOnWhitelist(turret, Commands.cmdBlacklist))	// Targeting blacklisted entities only
			{
				if (potentialEntity == turret) { skip = true; }						// Never targeting myself
				else if (!isEntityListed(turret, potentialEntity)) { skip = true; }	// Not blacklisted, so not targeting them
			}
			else	// Targeting hostiles
			{
				if (isEntityOnInternalWhitelist(turret, potentialEntity)) { skip = true; }
				else if (isEntityListed(turret, potentialEntity)) { skip = true; }	// Not attacking whitelisted targets
				
				if (potentialEntity instanceof EntityLiving && isNameOnWhitelist(turret, Commands.cmdInjuredOnly))
				{
					EntityLiving living = (EntityLiving) potentialEntity;
					if (living.getHealth() >= living.getMaxHealth()) { skip = true; } // Currently at max health
				}
			}
			
			if (!Helper.canEntityBeSeen(turret.worldObj, turret, potentialEntity)) { skip = true; }
			if (potentialEntity.isDead) { skip = true; }				// Not shooting at the dead
			
			// Not attacking people in creative mode
			if (potentialEntity instanceof EntityPlayer)
			{
				EntityPlayer potentialPlayer = (EntityPlayer) potentialEntity;
				
				if (potentialPlayer.capabilities.isCreativeMode) { skip = true; }
				if (!Main.allowTurretPlayerAttacks) { skip = true; }	// Not allowed to attack players in general
			}
			
			if (isNameOnWhitelist(turret, Commands.cmdTargetFlying) && potentialEntity.onGround) { skip = true; }	// Set to target flying, but that entity isn't. Not doing this.
			
			
			if (!skip)	// Checks out?
			{
				double distance = turret.getDistanceSqToEntity(potentialEntity);
				
				if (distance < closestDistance)
				{
					// Assuming this entity as potential target and its distance
					closestEntity = potentialEntity;
					closestDistance = distance;
				}
				// else, is further away
			}
			
			counter += 1;
		}
		
		turret.currentTarget = closestEntity;	// Could be null if no one's nearby to shoot at
	}
	
	
	private static boolean isEntityOnInternalWhitelist(Entity_AA turret, Entity entity)
	{
		if (entity == turret) { return true; }								// Not shooting at myself
		else if (entity.getClass() == turret.getClass()) { return true; }	// Not shooting at other turrets
		
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			if (player.getDisplayName().equals(turret.ownerName)) { return true; }	// Not shooting at my owner
		}
		
		return false;
	}
	

	private static boolean isEntityListed(Entity_AA turret, Entity entity)
	{
		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			
			if (isNameOnWhitelist(turret, player.getDisplayName())) { return true; }	// Not shooting at their friends either
			else if (isNameOnWhitelist(turret, "player")) { return true; }				// Not shooting at players in general
		}
		
		// Creature specific whitelists
		else if (entity instanceof EntityVillager && isNameOnWhitelist(turret, "villager")) { return true; }
		else if (entity instanceof EntityChicken && isNameOnWhitelist(turret, "chicken")) { return true; }
		else if (entity instanceof EntityCow && isNameOnWhitelist(turret, "cow")) { return true; }
		else if (entity instanceof EntityHorse && isNameOnWhitelist(turret, "horse")) { return true; }
		else if (entity instanceof EntityMooshroom && isNameOnWhitelist(turret, "mooshroom")) { return true; }
		else if (entity instanceof EntityOcelot && isNameOnWhitelist(turret, "ocelot")) { return true; }
		else if (entity instanceof EntityPig && isNameOnWhitelist(turret, "pig")) { return true; }
		else if (entity instanceof EntitySheep && isNameOnWhitelist(turret, "sheep")) { return true; }
		else if (entity instanceof EntitySquid && isNameOnWhitelist(turret, "squid")) { return true; }
		else if (entity instanceof EntityWolf && isNameOnWhitelist(turret, "wolf")) { return true; }
		
		// Bosses
		else if (entity instanceof EntityDragon && isNameOnWhitelist(turret, "ender dragon")) { return true; }
		else if (entity instanceof EntityDragonPart && isNameOnWhitelist(turret, "ender dragon")) { return true; }
		else if (entity instanceof EntityWither && isNameOnWhitelist(turret, "wither")) { return true; }
		
		// Hostiles
		else if (entity instanceof EntityBlaze && isNameOnWhitelist(turret, "blaze")) { return true; }
		else if (entity instanceof EntityCaveSpider && isNameOnWhitelist(turret, "cave spider")) { return true; }
		else if (entity instanceof EntityCreeper && isNameOnWhitelist(turret, "creeper")) { return true; }
		else if (entity instanceof EntityEnderman && isNameOnWhitelist(turret, "enderman")) { return true; }
		else if (entity instanceof EntityGhast && isNameOnWhitelist(turret, "ghast")) { return true; }
		else if (entity instanceof EntityGiantZombie && isNameOnWhitelist(turret, "zombie")) { return true; }
		else if (entity instanceof EntityGolem && isNameOnWhitelist(turret, "golem")) { return true; }
		else if (entity instanceof EntityIronGolem && isNameOnWhitelist(turret, "iron golem")) { return true; }
		else if (entity instanceof EntityMagmaCube && isNameOnWhitelist(turret, "magma cube")) { return true; }
		else if (entity instanceof EntityPigZombie && isNameOnWhitelist(turret, "pig zombie")) { return true; }
		else if (entity instanceof EntitySilverfish && isNameOnWhitelist(turret, "silverfish")) { return true; }
		else if (entity instanceof EntitySkeleton && isNameOnWhitelist(turret, "skeleton")) { return true; }
		else if (entity instanceof EntitySlime && isNameOnWhitelist(turret, "slime")) { return true; }
		else if (entity instanceof EntitySnowman && isNameOnWhitelist(turret, "snow golem")) { return true; }
		else if (entity instanceof EntitySpider && isNameOnWhitelist(turret, "spider")) { return true; }
		else if (entity instanceof EntityWitch && isNameOnWhitelist(turret, "witch")) { return true; }
		else if (entity instanceof EntityZombie && isNameOnWhitelist(turret, "zombie")) { return true; }
		
		if (entity instanceof EntityLiving)	// For things with custom names
		{
			EntityLiving living = (EntityLiving) entity;
			
			if (living.hasCustomNameTag())
			{
				if (isNameOnWhitelist(turret, living.getCustomNameTag())) { return true; }
			}
		}
		
		return false;	// Nope. Fire away!
	}
	
	
	public static boolean isNameOnWhitelist(Entity_AA turret, String nameOrCmd)
	{
		if (turret.storage == null) { return false; }	// Has no space to store anything in
		int counter = 0;
		
		// Step 1, find a book in our inventory
		
		while (counter < turret.storage.length)
		{
			if (turret.storage[counter] != null)
			{
				if (turret.storage[counter].getItem() == Items.writable_book || turret.storage[counter].getItem() == Items.written_book)
				{
					//System.out.println("[TURRET] Checking writable book for whitelist against " + playerName);
					
					if (turret.storage[counter].hasTagCompound())
					{
						NBTTagList pageList = turret.storage[counter].getTagCompound().getTagList("pages", 8);
						
						int pageCount = 0;
						
						String currentPage = pageList.getStringTagAt(pageCount);
						
						while (currentPage != null && !currentPage.isEmpty())
						{
							String[] lines = currentPage.split("\n");
							
							int lineCount = 0;
							
							while (lineCount < lines.length)
							{
								//System.out.println("[TURRET] Book page " + pageCount + " - line " + lineCount + ": " + lines[lineCount]);
								
								if (lines[lineCount].equals(nameOrCmd)) { return true;	} // Found it!
								// else, not on this line
								
								lineCount += 1;
							}
							
							pageCount += 1;
							currentPage = pageList.getStringTagAt(pageCount);	// Next!
						}
						// Done with all known pages (will stop when there's empty pages inbetween)
					}
					// else, no tag, so can't have anything in it
				}
				// else, not a book
			}
			// else, nothing in that slot
			
			
			counter += 1;
		}
		
		return false;	// Fallback. Didn't find a book with this name
	}
	
	
	public static boolean canAttackTarget(Entity_AA turret, _WeaponBase currentWeapon)
	{
		if (turret.currentTarget == null) { return false; }							// No one there
		if (currentWeapon == null) { return false; }								// What're you holding?
		if (!turret.hasFirstWeapon && !turret.hasSecondWeapon) { return false; }	// Don't have anything to shoot with
		
		if (turret.currentTarget == turret) { return false; }		// Can't target myself
		if (!Helper.canEntityBeSeen(turret.worldObj, turret, turret.currentTarget)) { return false; }	// ...ALTERNATE alternate function with as few adjustments as possible
		
		if (turret.currentTarget.isDead) 	// Enough!
		{
			AI_Targeting.targetNearestEntity(turret);	// Speed the retargeting up
			return false;
		}
		
		if (isNameOnWhitelist(turret, Commands.cmdSafeRange))	// Instructed to keep a minimum safe range
		{
			double minSafetyRange = getSafetyRange(turret, currentWeapon);
			double distance = turret.getDistanceSqToEntity(turret.currentTarget);
			
			if (minSafetyRange != -1 && distance < minSafetyRange) { return false; }	// Too close
		}
		
		// Add line of sight check (raytrace), to see if someone whitelisted is between me and the target
		
		return true;	// Seems to check out
	}
	
	
	public static void setAttackRange(Entity_AA turret)	// Guaranteed to have a weapon
	{
		if (turret.firstWeapon == null) // Unlikely, but just in case
		{
			turret.attackDistance = 8;
			return;
		}
		
		turret.attackDistance = 19 * (turret.firstWeapon.Speed * turret.firstWeapon.Speed);	// safety margin
		
		if (Main.restrictTurretRange && turret.attackDistance > 32) { turret.attackDistance = 32; }	// Limiter
	}
	
	
	public static MovingObjectPosition getMovingObjectPositionFromPlayer(World world, EntityPlayer player, double targetingDistance)
	{
		float f = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
		
		double playerX = player.prevPosX + (player.posX - player.prevPosX) * f;
		double playerY = player.prevPosY + (player.posY - player.prevPosY) * f + (world.isRemote ? player.getEyeHeight() - player.getDefaultEyeHeight() : player.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
		double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
		
		Vec3 vecPlayer = Vec3.createVectorHelper(playerX, playerY, playerZ);
		
		float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
		float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
		float f5 = -MathHelper.cos(-f1 * 0.017453292F);
		float f6 = MathHelper.sin(-f1 * 0.017453292F);
		float f7 = f4 * f5;
		float f8 = f3 * f5;
		
		double maxDistance = targetingDistance;
		
		Vec3 vecTarget = vecPlayer.addVector(f7 * maxDistance, f6 * maxDistance, f8 * maxDistance);
		
		return world.func_147447_a(vecPlayer, vecTarget, false, false, true);	// false, true, false
	}
	
	
	private static double getSafetyRange(Entity_AA turret, _WeaponBase currentWeapon)
	{
		if (currentWeapon instanceof RPG)
		{
			RPG rpg = (RPG) currentWeapon;
			return rpg.ExplosionSize * rpg.ExplosionSize;	// Squared
		}
		
		else if (currentWeapon instanceof RPG_Imp)
		{
			RPG_Imp rpg = (RPG_Imp) currentWeapon;
			return rpg.ExplosionSize * rpg.ExplosionSize;	// Squared
		}
		
		else if (currentWeapon instanceof ERA)
		{
			ERA era = (ERA) currentWeapon;
			return era.explosionTarget * era.explosionTarget;	// Squared
		}
		
		return -1;	// Fallback for "no minimum range limit"
	}
}
