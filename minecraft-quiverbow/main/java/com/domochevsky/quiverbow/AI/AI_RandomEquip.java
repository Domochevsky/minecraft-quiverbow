package com.domochevsky.quiverbow.AI;

import java.util.Random;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import net.minecraft.item.ItemStack;

public class AI_RandomEquip 
{
	public static void setupGear(Entity_AA turret)
	{
		if (turret.worldObj.isRemote) { return; }	// Not doing this on client side
		
		setUpgrades(turret);	// Step 1, sorting out what they can do
		setWeapons(turret);		// Step 2, what do they hold?
		setItems(turret);		// Step 3, ammo
	}
	
	
	private static void setUpgrades(Entity_AA turret)
	{
		// What do you have available?
		Random rand = new Random();
		
		AI_Properties.applyMobilityUpgrade(turret);	// Always
		
		int odds = rand.nextInt(10);	// 0-9 vs 7+
		if (odds >= 7) { AI_Properties.applyArmorUpgrade(turret); }
		
		odds = rand.nextInt(20);		// 0-19 vs 19
		if (odds >= 19) { AI_Properties.applyPlatingUpgrade(turret); }
		
		odds = rand.nextInt(20);		// 0-19 vs 19
		if (odds >= 19) { AI_Properties.applyWeaponUpgrade(turret); }
	}
	
	
	private static void setWeapons(Entity_AA turret)
	{    	
    	Random rand = new Random();
    	int randNum = 0;
    	
    	int attempts = 0;	// How often we tried
    	_WeaponBase weapon = null;
    	
    	while (weapon == null)	// Until we hit a valid weapon
    	{
    		randNum = rand.nextInt(Main.weapons.length);	// Grabbing a number between the list's length and its starting point
    		
    		weapon = Main.weapons[randNum];
    		
    		if (weapon != null && !weapon.isMobUsable()) { weapon = null; }	// Cannot be used by mobs, so begone
    		else if (weapon != null && !weapon.Enabled) { weapon = null; }	// Is disabled, so begone
    		
    		attempts += 1;
    		
    		if (attempts >= 1000) 
    		{
    			System.out.println("[QUIVERBOW] Weapon randomizer: Couldn't find a valid first weapon for an Arms Assistant. Giving up.");
    			return;
    		}
    	}
    	
    	AI_WeaponHandler.setFirstWeapon(turret, new ItemStack(weapon));	// Should have a weapon now
    	
    	if (!turret.hasWeaponUpgrade) { return; }	// Doesn't have a second rail, so we're done here
    	
    	attempts = 0;	// Reset
    	weapon = null;
    	
    	while (weapon == null)	// Until we hit a valid weapon
    	{
    		randNum = rand.nextInt(Main.weapons.length);	// Grabbing a number between the list's length and its starting point
    		
    		weapon = Main.weapons[randNum];
    		
    		if (weapon != null && !weapon.isMobUsable()) { weapon = null; }	// Cannot be used by mobs, so begone
    		else if (weapon != null && !weapon.Enabled) { weapon = null; }	// Is disabled, so begone
    		
    		attempts += 1;
    		
    		if (attempts >= 1000) 
    		{
    			System.out.println("[QUIVERBOW] Weapon randomizer: Couldn't find a valid second weapon for an Arms Assistant. Giving up.");
    			return;
    		}
    	}
    	
    	AI_WeaponHandler.setSecondWeapon(turret, new ItemStack(weapon));
    	turret.setCurrentItemOrArmor(1, new ItemStack(turret.secondWeapon));	// Should have a second weapon as well now
	}
	
	
	private static void setItems(Entity_AA turret)
	{
		// Might not be necessary, since we're not dropping that stuff anyway
	}
}
