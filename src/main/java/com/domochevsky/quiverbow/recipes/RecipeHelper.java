package com.domochevsky.quiverbow.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeHelper
{
	// Returns true if these components are what I'm looking for to make my item
	public static boolean doesRecipeMatch(ItemStack[] recipeItems, InventoryCrafting matrix, World world)
	{
		int requiredMatches = 9;
		int matches = 0;

		int currentSlot = 0;
		ItemStack currentStack;

		while (currentSlot < 9)	// Going through all 9 slots
		{
			currentStack = matrix.getStackInSlot(currentSlot);	// Hand me your item, slot

			if (currentStack != null && recipeItems[currentSlot] != null)
			{
				if (currentStack.getItem() != recipeItems[currentSlot].getItem())
				{
					return false; 	// Not the right item
				}
				else if (currentStack.isItemStackDamageable() && currentStack.getItemDamage() != recipeItems[currentSlot].getItemDamage())
				{
					return false; 	// Damage doesn't match up
				}
				else if (currentStack.isStackable() && currentStack.stackSize < recipeItems[currentSlot].stackSize)
				{
					return false; 	// Not the right amount
				}
				else
				{
					matches += 1; 	// Seems to check out
				}
			}
			else if (currentStack == null && recipeItems[currentSlot] == null)
			{
				matches += 1;  // Both null, so that works for me too
			}

			// Next!
			currentSlot += 1;
		}

		//if (!world.isRemote) { System.out.println("[RECIPE] Found " + matches + " matches out of " + requiredMatches + "."); }

		if (matches == requiredMatches) { return true; }	// Found all we need
		else { return false; }								// Not enough
	}
}
