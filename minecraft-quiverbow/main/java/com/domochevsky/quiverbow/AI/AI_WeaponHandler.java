package com.domochevsky.quiverbow.AI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.ammo.ColdIronClip;
import com.domochevsky.quiverbow.ammo.GatlingAmmo;
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.ammo.LapisMagazine;
import com.domochevsky.quiverbow.ammo.LargeNetherrackMagazine;
import com.domochevsky.quiverbow.ammo.LargeRedstoneMagazine;
import com.domochevsky.quiverbow.ammo.LargeRocket;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.ammo.RedstoneMagazine;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.ammo.SeedJar;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.*;

public class AI_WeaponHandler 
{
	public static void setFirstWeapon(Entity_AA turret, ItemStack weapon)	// Sets our weapon directly via ID, no questions asked
	{
		AI_Storage.dropFirstWeapon(turret);	// Begone with what I'm currently holding
		
		turret.firstWeapon = (_WeaponBase) weapon.getItem();
		
		turret.setCurrentItemOrArmor(0, weapon);	// Equip
		AI_Targeting.setAttackRange(turret);
		
		turret.hasFirstWeapon = true;
		turret.hasToldOwnerAboutFirstAmmo = false;	// Reset
	}
	
	
	public static void setSecondWeapon(Entity_AA turret, ItemStack weapon)	// Sets our weapon directly via ID, no questions asked
	{
		AI_Storage.dropSecondWeapon(turret);	// Begone with what I'm currently holding
		
		turret.secondWeapon = (_WeaponBase) weapon.getItem();
		
		turret.setCurrentItemOrArmor(1, weapon);	// Equip
		
		turret.hasSecondWeapon = true;
		turret.hasToldOwnerAboutSecondAmmo = false;	// Reset
	}
	
	
	public static void attackTarget(Entity_AA turret, boolean secondRail)
	{
		attackTarget(turret, secondRail, false);
	}
	
	
	public static void attackTarget(Entity_AA turret, boolean secondRail, boolean ignoreValidation)
	{
		if (!ignoreValidation)
		{
			if (secondRail) { if (!AI_Targeting.canAttackTarget(turret, turret.secondWeapon)) { return; } } 
			else { if (!AI_Targeting.canAttackTarget(turret, turret.firstWeapon)) { return; } }	// Not happening
		}
		// else, not doing target validation
		
		_WeaponBase currentWeapon = null;
		ItemStack currentStack = null;
		
		if (secondRail)
		{
			if (turret.firstWeapon != null && turret.getHeldItem() != null)	// Checking fire staggering
			{
				if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdStaggerFire))	// Instructed to fire our weapons alternating
				{
					if (turret.firstWeapon.getCooldown(turret.getHeldItem()) != (turret.firstWeapon.getMaxCooldown() / 2))
					{
						return;	// Not ready yet. Only firing when the primary weapon cooldown has reached half of its max
					}
				}
			}
			
			currentWeapon = turret.secondWeapon;
			currentStack = turret.getEquipmentInSlot(1);
		}
		else
		{
			currentWeapon = turret.firstWeapon;
			currentStack = turret.getHeldItem();
		}
		
		// Still need to check we're actually holding anything
		if (ignoreValidation && (currentWeapon == null || currentStack == null)) { return; }	// Nothing to attack with? Weird
		
		int currentDmg = currentWeapon.getDamage(currentStack);
		
		// We're out of ammo
		if (currentDmg >= currentWeapon.getMaxDamage()) 
		{ 
			turret.worldObj.playSoundAtEntity(turret, "random.click", 0.6F, 0.3F);
			
			if (secondRail) { turret.secondAttackDelay = 40; }
			else { turret.firstAttackDelay = 40; }
			
			reloadFromStorage(turret, secondRail);	// Try to reload if you can
			
			return; 
		}
		
		// Special case
		if (currentWeapon instanceof Crossbow_Auto)
		{
			if (currentStack.hasTagCompound() && !currentStack.getTagCompound().getBoolean("isChambered"))
			{
				// Is the auto crossbow and not chambered. Doing that now
				currentStack.getTagCompound().setBoolean("isChambered", true);	// Done, we're good to go again
				
				// SFX
				turret.worldObj.playSoundAtEntity(turret, "random.click", 0.8F, 0.5F);
				
				// This long until you can fire again
				if (secondRail) { turret.secondAttackDelay = currentWeapon.getMaxCooldown() + 1; }
				else { turret.firstAttackDelay = currentWeapon.getMaxCooldown() + 1; }	
				
				return;	// We're done here
			}
		}
		
		if (secondRail) { turret.posY -= 0.4; }	// Pos adjustment for the second rail
		
		if (!ignoreValidation) { AI_Targeting.lookAtTarget(turret, secondRail); }	// Just making sure
		
		currentWeapon.doSingleFire(currentStack, turret.worldObj, turret);	// BLAM
		
		if (secondRail) { turret.posY += 0.4; }	// Pos adjustment for the second rail
		
		//consumeAmmo(turret, secondRail);	// Should now be properly handled by the weapons themselves
		
		// This long until you can fire again
		if (secondRail) { turret.secondAttackDelay = currentWeapon.getMaxCooldown() + 1; }
		else { turret.firstAttackDelay = currentWeapon.getMaxCooldown() + 1; }
	}
	
	
	// Known to be on server side
	private static void reloadFromStorage(Entity_AA turret, boolean secondRail)
	{
		_WeaponBase currentWeapon = null;
		ItemStack currentStack = null;
		
		if (secondRail)
		{
			currentWeapon = turret.secondWeapon;
			currentStack = turret.getEquipmentInSlot(1);
		}
		else
		{
			currentWeapon = turret.firstWeapon;
			currentStack = turret.getHeldItem();
		}
		
		if (turret.ownerName != null && turret.ownerName.equals("Herobrine"))	// We're naturally hostile, so reloading is infinite
		{
			currentStack.setItemDamage(0);	// Fill
			return;
		}
		
		int slot = 0;
		boolean sendMsg = false;
		
		while (slot < turret.storage.length)	// Lesse what I got here...
		{
			if (turret.storage[slot] != null)
			{
				if (currentWeapon instanceof LapisCoil && turret.storage[slot].getItem() instanceof LapisMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof OSP && turret.storage[slot].getItem() instanceof ObsidianMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof OSR && turret.storage[slot].getItem() instanceof ObsidianMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof OWR && turret.storage[slot].getItem() instanceof ObsidianMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof NetherBellows && turret.storage[slot].getItem() instanceof LargeNetherrackMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof RedSprayer && turret.storage[slot].getItem() instanceof LargeRedstoneMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof LightningRed && turret.storage[slot].getItem() instanceof RedstoneMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof CoinTosser && turret.storage[slot].getItem() instanceof GoldMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof CoinTosser_Mod && turret.storage[slot].getItem() instanceof GoldMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof SugarEngine && turret.storage[slot].getItem() instanceof GatlingAmmo)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof ThornSpitter && turret.storage[slot].getItem() instanceof NeedleMagazine)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof SeedSweeper && turret.storage[slot].getItem() instanceof SeedJar)
				{
					currentStack.setItemDamage(turret.storage[slot].getItemDamage());	// Fill
					turret.storage[slot].setItemDamage(turret.storage[slot].getMaxDamage());	// Empty
					sendMsg = true;
				}
				
				else if (currentWeapon instanceof RPG_Imp && turret.storage[slot].getItem() instanceof LargeRocket)
				{
					currentStack.setItemDamage(0);	// Fill
					decreaseStackSize(turret, slot, 1);
				}
				
				else if (currentWeapon instanceof DragonBox && turret.storage[slot].getItem() instanceof RocketBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8));	// 8 shots per bundle
				}
				
				else if (currentWeapon instanceof DragonBox_Quad && turret.storage[slot].getItem() instanceof RocketBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 2));	// 2 shots per bundle
				}
				
				else if (currentWeapon instanceof Crossbow_Compact && turret.storage[slot].getItem() == Items.arrow)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
				}
				
				else if (currentWeapon instanceof Crossbow_Double && turret.storage[slot].getItem() == Items.arrow)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 2));
				}
				
				else if (currentWeapon instanceof Crossbow_Blaze && turret.storage[slot].getItem() == Items.blaze_rod)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
				}
				
				else if (currentWeapon instanceof Crossbow_Auto && turret.storage[slot].getItem() instanceof ArrowBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 8));	// 1 bundle for 8 shots
				}
				
				else if (currentWeapon instanceof Crossbow_AutoImp && turret.storage[slot].getItem() instanceof ArrowBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 2) * 8));	// 2 bundles for 16 shots
				}
				
				else if (currentWeapon instanceof FrostLancer && turret.storage[slot].getItem() instanceof ColdIronClip)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - (decreaseStackSize(turret, slot, 1) * 4));	// 1 ammo for 4 shots
				}
				
				else if (currentWeapon instanceof EnderRifle && turret.storage[slot].getItem() == Items.iron_ingot)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8));
				}
				
				else if (currentWeapon instanceof SilkenSpinner && turret.storage[slot].getItem() == Item.getItemFromBlock(Blocks.web))
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 1));
				}
				
				else if (currentWeapon instanceof SnowCannon && turret.storage[slot].getItem() == Item.getItemFromBlock(Blocks.snow))
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 16));
				}	
				
				else if (currentWeapon instanceof Mortar_Arrow && turret.storage[slot].getItem() instanceof ArrowBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8));	// 1 shot per bundle, can hold 8 bundles
				}
				
				else if (currentWeapon instanceof Mortar_Dragon && turret.storage[slot].getItem() instanceof RocketBundle)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 8));	// 1 shot per bundle, can hold 8 bundles
				}
				
				else if (currentWeapon instanceof Potatosser && turret.storage[slot].getItem() == Items.potato)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 14));
				}
				
				else if (currentWeapon instanceof SoulCairn && turret.storage[slot].getItem() == Items.diamond)
				{
					currentStack.setItemDamage(currentStack.getItemDamage() - decreaseStackSize(turret, slot, 14));
				}
				
				// ---
				
				if (sendMsg)	// something changed, so telling all clients about it
				{
					NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.worldObj, turret, 
							Item.getIdFromItem(turret.storage[slot].getItem()), slot, turret.storage[slot].getItemDamage());
				}
				
				if (currentStack.getItemDamage() < currentStack.getMaxDamage())
				{
					// Reset
					if (secondRail) { turret.hasToldOwnerAboutSecondAmmo = false; }
					else { turret.hasToldOwnerAboutFirstAmmo = false; }
					
					return;	// We've loaded something in, so we're done here
				}
			}
			// else there's nothing in that slot
			
			slot += 1;
		}
		
		// Done going through storage and didn't find anything to reload with.
		if (turret.hasCommunicationUpgrade)
		{
			if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdTellAmmo))	// Informing the owner when they're out of ammo
			{
				if (secondRail && !turret.hasToldOwnerAboutSecondAmmo) 
				{ 
					AI_Communication.tellOwnerAboutAmmo(turret, secondRail);
					turret.hasToldOwnerAboutSecondAmmo = true;
				}
				else if (!secondRail && !turret.hasToldOwnerAboutFirstAmmo)
				{ 
					AI_Communication.tellOwnerAboutAmmo(turret, secondRail);
					turret.hasToldOwnerAboutFirstAmmo = true;
				}
			}
		}
		// else, no com upgrade. Nevermind
	}
	
	
	// Returns the amount removed (in case the leftovers are less than what was asked for)
	private static int decreaseStackSize(Entity_AA turret, int slot, int amount)
	{
		if (slot < 0 || slot > turret.storage.length) { return 0; }		// Out of bounds
		if (turret.storage[slot] == null) { return 0; }					// Nothing in there 
		
		int amountRemoved = amount;
		
		if (turret.storage[slot].stackSize < amount)	// Less left than we need
		{
			amountRemoved = turret.storage[slot].stackSize;	// Adjusting how much we remove
			
			turret.storage[slot].stackSize = 0;
			
			// Something changed, so informing the client about that now
			NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.worldObj, turret, -1, slot, 0);
			
			//return amountRemoved;
		}
		else	// Has as much as we need
		{
			turret.storage[slot].stackSize -= amount;
			
			// Still some left?
			if (turret.storage[slot].stackSize <= 0)	// Nope
			{
				turret.storage[slot] = null;	// Remove it
				
				// Something changed, so informing the client about that now
				NetHelper.sendTurretInventoryMessageToPlayersInRange(turret.worldObj, turret, -1, slot, 0);
			}
		}		
		
		return amountRemoved;
	}
	
	
	// different weapons consume ammo in different amounts
	// Not using their native methods, since that would drop magazines
	private static void consumeAmmo(Entity_AA turret, boolean secondRail)
	{
		_WeaponBase currentWeapon = turret.firstWeapon;
		ItemStack weaponStack = turret.getHeldItem();
		
		if (secondRail)
		{
			currentWeapon = turret.secondWeapon;
			weaponStack = turret.getEquipmentInSlot(1);	// Slot 0 is the first weapon
		}
		
		if (currentWeapon instanceof CoinTosser) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 9); }
		else if (currentWeapon instanceof CoinTosser_Mod) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 3); }
		else if (currentWeapon instanceof NetherBellows) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 5); }
		else if (currentWeapon instanceof RedSprayer) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 5); }
		else if (currentWeapon instanceof ThornSpitter) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 4); }
		else if (currentWeapon instanceof SeedSweeper) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 8); }
		else if (currentWeapon instanceof DragonBox_Quad) { weaponStack.setItemDamage(weaponStack.getItemDamage() + 4); }
		else if (currentWeapon instanceof Sunray) { }
		else 
		{
			weaponStack.setItemDamage(weaponStack.getItemDamage() + 1);	// Default, one shot consumed
		}
		
		// Just making sure you're not going into space
		if (weaponStack.getItemDamage() > weaponStack.getMaxDamage()) { weaponStack.setItemDamage(weaponStack.getMaxDamage()); }
	}
	
	
	public static void fireWithOwner(Entity_AA turret, boolean secondRail)
	{
		if (!(turret.riddenByEntity instanceof EntityPlayer)) { return; }	// Not a living thing riding us, so can't hold weapons
		
		EntityPlayer rider = (EntityPlayer) turret.riddenByEntity;
		
		if (rider.getHeldItem() == null) { return; }	// Not holding anything
		
		if (!(rider.getHeldItem().getItem() instanceof AA_Targeter)) { return; }	// Isn't holding the targeter
		
		AA_Targeter weapon = (AA_Targeter) rider.getHeldItem().getItem();
		
		if (weapon.getCooldown(rider.getHeldItem()) <= 0) { return; }	// Isn't firing
		
		turret.rotationPitch = rider.rotationPitch;
		
		// Checks out. The owner's weapon is on cooldown, meaning they've fired very recently. Getting in on that. 
		// We should already be looking where the owner is looking
		attackTarget(turret, secondRail, true);
	}
}
