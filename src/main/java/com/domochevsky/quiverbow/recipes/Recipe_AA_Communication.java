package com.domochevsky.quiverbow.recipes;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;

import com.domochevsky.quiverbow.ammo.PackedUpAA;

public class Recipe_AA_Communication extends ShapelessRecipes implements IRecipe
{
	public Recipe_AA_Communication(ItemStack result, List components) 
	{
		super(result, components);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting matrix)
    {
		ItemStack stack = this.getRecipeOutput().copy();
		ItemStack previousAA = this.getAAFromMatrix(matrix);
		
		if (previousAA != null && previousAA.hasTagCompound())	// Copying existing properties
		{
			stack.setTagCompound((NBTTagCompound) previousAA.getTagCompound().copy());
		}
		else	// ...or just applying new ones
		{
			stack.setTagCompound(new NBTTagCompound());
		}
		
		// Apply the new upgrade now
		stack.getTagCompound().setBoolean("hasCommunicationUpgrade", true);
		
        return stack;
    }
	
	
	private ItemStack getAAFromMatrix(InventoryCrafting matrix)
	{
		int counter = 0;
		
		while (counter < matrix.getSizeInventory())
		{
			if (matrix.getStackInSlot(counter) != null && matrix.getStackInSlot(counter).getItem() instanceof PackedUpAA)
			{
				return matrix.getStackInSlot(counter);	// Found it
			}
			
			counter += 1;
		}
		
		return null;
	}
}
