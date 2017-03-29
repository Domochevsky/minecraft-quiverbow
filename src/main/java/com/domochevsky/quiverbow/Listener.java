package com.domochevsky.quiverbow;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.domochevsky.quiverbow.weapons.ERA;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Listener
{
	@SubscribeEvent
	public void ItemCraftedEvent(cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event)
	{
		//System.out.println("[EVENT] Player crafted something.");

		if (event.crafting != null && event.crafting.getItem() instanceof ERA)
		{
			ItemStack stack = event.craftMatrix.getStackInSlot(1);

			if (stack != null && stack.stackSize > 1)
			{
				stack.stackSize -= 26;
				if (stack.stackSize <= 0) { event.craftMatrix.setInventorySlotContents(1, null); }	// Nothing left
			}
			// else, nothing in there or only a single rail, meaning this is a repairing event. I'm fine with that
		}
		// else, not mine, so don't care

		else if (event.crafting != null && event.crafting.getItem() instanceof _WeaponBase)	// More generic weapon check
		{
			this.copyName(event.craftMatrix, event.crafting);
		}
	}


	private void copyName(IInventory craftMatrix, ItemStack newItem)	// Does the weapon have a custom name? If so then we're transfering that to the new item
	{
		// Step 1, find the actual item (It's possible that this is not a reloading action, meaning there is no weapon to copy the name from)

		int slot = 0;

		while (slot < 9)
		{
			ItemStack stack = craftMatrix.getStackInSlot(slot);

			if (stack != null && stack.getItem() instanceof _WeaponBase)	// Found it. Does it have a name tag?
			{
				if (stack.hasDisplayName() && !newItem.hasDisplayName()) { newItem.setStackDisplayName(stack.getDisplayName()); }
				// else, has no custom display name or the new item already has one. Fine with me either way.

				return;	// Either way, we're done here
			}
			// else, either doesn't exist or not what I'm looking for

			slot += 1;
		}
	}
}
