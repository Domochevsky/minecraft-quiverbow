package com.domochevsky.quiverbow.recipes;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.weapons.OSP;
import com.domochevsky.quiverbow.weapons._WeaponBase;

public class Recipe_Ammo extends ShapelessRecipes implements IRecipe
{
	private Item ammo;
	private Item weapon;
	private int metadata;
	
	public Recipe_Ammo(Item ammo, Item weapon, List recipe) 
	{
		super(new ItemStack(weapon), recipe);
		
		this.ammo = ammo;
		this.weapon = weapon;
	}
	
	
	@Override
	public boolean matches(InventoryCrafting matrix, World world)	// Returns true if these components are what I'm looking for to make my item
    {
		if (!this.isInMatrix(matrix, this.weapon)) { return false; }// Weapon ain't in the matrix
		if (!this.isInMatrix(matrix, this.ammo)) { return false; }	// Ammo ain't in the matrix
		
		return true;	// Checks out
    }
	
	
	// Returns true if the requested item is anywhere in the matrix
	private boolean isInMatrix(InventoryCrafting matrix, Item item) 
	{
		if (item == null) { return false; }	// Can't find what doesn't exist
		
		int counter = 0;
		
		ItemStack stack = matrix.getStackInSlot(counter);
		
		while (counter < matrix.getSizeInventory())	// scouring through the entire thing
		{
			if (stack != null && stack.getItem().getClass() == item.getClass()) // Found one!
			{ 
				if (stack.getItem() instanceof _WeaponBase)	// Is a weapon, so need to ensure that it's empty
				{
					if (stack.getItemDamage() == stack.getMaxDamage()) { return true; }
					// else, isn't empty
				}
				else if (stack.getItem() instanceof _AmmoBase)	// is ammo
				{
					this.metadata = stack.getItemDamage();	// Keeping track of what this is gonna make, so I don't have to constantly recheck
					return true; 
				}
				// else, don't care what this is
			}
			// else, empty. That's fine
			
			// Next!
			counter += 1;
			stack = matrix.getStackInSlot(counter);
		}
		
		return false;	// Fallback. Didn't find what I'm looking for
	}
	
	
	@Override
	public ItemStack getRecipeOutput() { return new ItemStack(this.weapon, 1, this.metadata); }


	@Override
	public ItemStack getCraftingResult(InventoryCrafting matrix)
    {
		if (this.weapon instanceof OSP) { this.metadata *= 2; }	// Two shots per boolet
		
		ItemStack stack = new ItemStack(this.weapon, 1, this.metadata);
		
		Helper.copyProps(matrix, stack);
		
        return stack;
    }
}
