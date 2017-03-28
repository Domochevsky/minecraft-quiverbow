package com.domochevsky.quiverbow.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;

public class Recipe_ERA extends ShapedRecipes implements IRecipe
{
	public Recipe_ERA(ItemStack[] components, ItemStack result) // Stacksize sensitive version for recipes
	{
		super(3, 3, components, result);
	}
	
	
	@Override
	public boolean matches(InventoryCrafting matrix, World world)
    {
		if (matrix.getSizeInventory() != 9) { return false; }	// Only care about full size crafting recipes
		
		//System.out.println("[RECIPE] Checking for recipe matches.");
		
		int requiredMatches = 9;
		int matches = 0;
		
		int currentSlot = 0;
		ItemStack currentStack = matrix.getStackInSlot(currentSlot);
		
		while (currentSlot < 9)	// Going through all 9 slots
		{
			if (currentStack != null && this.recipeItems[currentSlot] != null)
			{
				if (currentStack.getItem().getClass() != this.recipeItems[currentSlot].getItem().getClass()) { return false; }	// Not the right item
				else if (currentStack.getItemDamage() != this.recipeItems[currentSlot].getItemDamage()) { return false; }		// Damage doesn't match up
				else if (currentStack.isStackable() && currentStack.stackSize < this.recipeItems[currentSlot].stackSize) { return false; }	// Not the right amount
				else 
				{ 					
					matches += 1; 	// Seems to check out
				}	
			}
			else if (currentStack == null && this.recipeItems[currentSlot] == null) { matches += 1;  }	// Both null, so that works for me too
			
			// Next!
			currentSlot += 1;
			currentStack = matrix.getStackInSlot(currentSlot);
		}
		
		//if (!world.isRemote) { System.out.println("[RECIPE] Found " + matches + " matches out of " + requiredMatches + "."); }
		
		if (matches == requiredMatches) { return true; }	// Found all we need
		else { return false; }								// Not enough
    }

	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting matrix)	// Making it happen
	{
		ItemStack stack = this.getRecipeOutput().copy();
		
		Helper.copyProps(matrix, stack);
		
		return stack;
	}
}
