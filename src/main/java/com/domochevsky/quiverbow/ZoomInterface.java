package com.domochevsky.quiverbow;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ZoomInterface 
{
	// A weapon on client side will poll if it needs to zoom in/out each tick
	// I also need to record the default FOV right before we zoom in, to use on zoomOut
	
	private static float defaultFOV = 0;	// Keeping track of the current FOV locally
	
	// The difference between these values will determine whether or not to toggle
	static boolean[] wasClientZoomed = new boolean[9];	// last tick
	static boolean[] isClientZoomed = new boolean[9];	// current tick
	
	
	// Polled by the weapon each tick
	public static void checkClientZoom(World world, Entity entity, ItemStack stack, int zoomFOV)
	{
		if (!world.isRemote) { return; }	// Only doing this on client (remote) side
		if (!(entity instanceof EntityPlayer)) { return; }	// Not a player
		
    	EntityPlayer player = (EntityPlayer) entity;
    	int slot = getSlotFromWeapon(player, stack);
    	
    	if (slot == 9) { return; }			// Not on the hotbar
    	
    	//System.out.println("[ZOOM] Polled by weapon in slot " + slot + ".");
    	
    	if (player.inventory.getCurrentItem() != stack) // Not holding this weapon, so now what?
    	{ 
    		//System.out.println("[ZOOM] Player is not holding the weapon.");
    		isClientZoomed[slot] = false;
    		verifyZoom(slot, false, zoomFOV);
    		//return; 
    	}
    	
    	else if (!player.isSneaking()) 	// Not sneaking
		{ 
    		//System.out.println("[ZOOM] Player is not sneaking.");
    		isClientZoomed[slot] = false;
    		verifyZoom(slot, true, zoomFOV);
		}
    	
    	else	// Both holding the item and sneaking. Zooming in now
    	{
    		//System.out.println("[ZOOM] Player is holding the weapon and sneaking. Should zoom now.");
    		isClientZoomed[slot] = true;
    		verifyZoom(slot, true, zoomFOV);
    	}
    	
    	// Finalizing, for the next tick
    	wasClientZoomed[slot] = isClientZoomed[slot];
	}
	
	
	// Returns the slot the polling item is sitting in. Only cares about hotbar slots
	static int getSlotFromWeapon(EntityPlayer player, ItemStack stack)
	{
		int counter = 0;
		
		while (counter < 9)	// Only going through the hotbar slots
		{
			if (stack == player.inventory.getStackInSlot(counter)) { return counter; }	// Found this item's slot
			counter += 1;
		}
		
		return 9;
	}
	
	
	// This compares slots, to decide if action needs to be taken
	static void verifyZoom(int slot, boolean holdingItem, int zoomFOV)
	{
		//System.out.println("[ZOOM] Verifying zoom. Slot " + slot + " / holdingItem " + holdingItem + " / zoomLevel " + zoomLevel + ".");
		
		if (wasClientZoomed[slot] && !isClientZoomed[slot])	// Was zoomed, but now is not
		{
			//System.out.println("[ZOOM] Was zoomed, but now isn't.");
			zoomClientOut();
			// else, not holding this item. There's a chance we're now holding a non-zoom item but aren't polled
		}
		
		else if (!wasClientZoomed[slot] && isClientZoomed[slot])	// Wasn't zoomed, but now is
		{
			//System.out.println("[ZOOM] Was not zoomed, but now is.");
			if (holdingItem) { zoomClientIn(zoomFOV); }
			// else not holding this item. How could it be marked as zoomed in?
		}
		
		else if (wasClientZoomed[slot] && isClientZoomed[slot]) 
		{ 
			//System.out.println("[ZOOM] Was zoomed and still is.");
		} // Was zoomed and still is. No change here
		
		else if (!wasClientZoomed[slot] && !isClientZoomed[slot]) 
		{ 
			//System.out.println("[ZOOM] Wasn't zoomed and still aren't.");
		} // Wasn't zoomed and still isn't
	}
	
	
	// Setting custom FOV now and recording the default one
	static void zoomClientIn(int zoomFOV)
	{
		if (wasAnySlotZoomed())		// At least one slot was already zoomed in, so not zooming in now
		{
			//System.out.println("[ZOOM] At least one slot was still zoomed in, so assuming we're already zoomed in.");
		}
		else						// Nothing was zoomed in last turn, so doing that now
		{
			//System.out.println("[ZOOM] Nothing was zoomed in last tick, so zooming in now.");
			defaultFOV = Minecraft.getMinecraft().gameSettings.fovSetting;	// Recording default FOV
			Minecraft.getMinecraft().gameSettings.fovSetting =  zoomFOV;	// And setting the setting to our new zoom FOV
		}
	}
	
	
	// Restoring default FOV now, but first need to check if any of the slots still have zoom set to true
	static void zoomClientOut()
	{
		if (isAnySlotZoomed())	// At least one slot is still marked as zoomed, so not zooming out yet
		{
			//System.out.println("[ZOOM] At least one slot is still marked as zoomed, so not zooming out yet.");
		}
		else					// Nothing left that is marked as zoomed in, so we can safely zoom out now
		{
			Minecraft.getMinecraft().gameSettings.fovSetting = defaultFOV;	// Restoring recorded default FOV
		}
	}
	
	
	static boolean isAnySlotZoomed()
	{
		int counter = 0;
		
		while (counter <= 8)
		{
			if (isClientZoomed[counter]) { return true; }	// Found a zoomed in slot
			counter += 1;
		}
		
		return false;	// Fallback
	}
	
	
	static boolean wasAnySlotZoomed()
	{
		int counter = 0;
		
		while (counter <= 8)
		{
			if (wasClientZoomed[counter]) { return true; }	// Found a zoomed in slot
			counter += 1;
		}
		
		return false;	// Fallback
	}
}
