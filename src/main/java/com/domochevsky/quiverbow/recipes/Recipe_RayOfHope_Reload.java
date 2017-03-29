package com.domochevsky.quiverbow.recipes;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.weapons.MediGun;

public class Recipe_RayOfHope_Reload extends ShapelessRecipes implements IRecipe
{
	private ItemStack regenPot_1;
	private ItemStack regenPot_2;
	
	private int restoreAmount = 0;
	
	public Recipe_RayOfHope_Reload(ItemStack result, List components, ItemStack regenPot_1, ItemStack regenPot_2) 
	{
		super(result, components);
		
		// Keeping track of what potions I'm looking for
		this.regenPot_1 = regenPot_1;
		this.regenPot_2 = regenPot_2;
	}	
	
	
	@Override
	public boolean matches(InventoryCrafting matrix, World world)
    {
		ItemStack medigun = this.getROHFromMatrix(matrix);
		
		if (medigun == null) { return false; }	// Has no AA in there
		// else, medigun is in there. Good.
		
		if (!medigun.hasTagCompound()) { medigun.setTagCompound(new NBTTagCompound()); }	// Init
		
		medigun.getTagCompound().setInteger("reloadAmount", 0);
		
		boolean matches = false;
		
		this.restoreAmount = 0;	// Reset
		
		int counter = 0;
		
		while (counter < matrix.getSizeInventory())
		{
			ItemStack potentialStack = matrix.getStackInSlot(counter);
			
			if (potentialStack != null && potentialStack.getItem() == this.regenPot_1.getItem() &&
					potentialStack.getItemDamage() == this.regenPot_1.getItemDamage())
			{
				// This is a light healing potion. Each of these adds a certain ammo restoration amount
				//this.restoreAmount += 20;
				medigun.getTagCompound().setInteger("reloadAmount", medigun.getTagCompound().getInteger("reloadAmount") + 20);
				
				matches = true;	// Found at least one thing to reload with
			}
			else if (potentialStack != null && potentialStack.getItem() == this.regenPot_2.getItem() &&
					potentialStack.getItemDamage() == this.regenPot_2.getItemDamage())
			{
				// This is a medium healing potion. Each of these adds a certain ammo restoration amount
				//this.restoreAmount += 40;
				medigun.getTagCompound().setInteger("reloadAmount", medigun.getTagCompound().getInteger("reloadAmount") + 40);
				
				matches = true;	// Found at least one thing to reload with
			}
			
			counter += 1;
		}

        return matches;
    }
	

	@Override
	public ItemStack getCraftingResult(InventoryCrafting matrix)
    {
		ItemStack stack = this.getRecipeOutput().copy();
		ItemStack medigun = this.getROHFromMatrix(matrix);
		
		if (medigun.getItemDamage() - medigun.getTagCompound().getInteger("reloadAmount") < 0) { return null; }	// Beyond reloading capacity
		
		if (medigun != null && medigun.hasTagCompound())	// Copying existing properties (should copy names too)
		{
			stack.setTagCompound((NBTTagCompound) medigun.getTagCompound().copy());
		}
		
		// Apply the new upgrade now
		stack.setItemDamage(medigun.getItemDamage() - medigun.getTagCompound().getInteger("reloadAmount"));	// Adding ammo
		
		// Reset
		medigun.getTagCompound().setInteger("reloadAmount", 0);
		stack.getTagCompound().setInteger("reloadAmount", 0);
		
        return stack;	// Here ya go
    }
	
	
	private ItemStack getROHFromMatrix(InventoryCrafting matrix)
	{
		int counter = 0;
		
		while (counter < matrix.getSizeInventory())
		{
			if (matrix.getStackInSlot(counter) != null && matrix.getStackInSlot(counter).getItem() instanceof MediGun)
			{
				return matrix.getStackInSlot(counter);	// Found it
			}
			
			counter += 1;
		}
		
		return null;	// Isn't in here
	}
}
