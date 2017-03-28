package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class LargeRocket extends _AmmoBase
{	
	@Override
	String getIconPath() { return "Bundle_BigRocket"; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add("A big rocket. Very dangerous.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Big Rocket"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A big rocket
    	GameRegistry.addRecipe(new ItemStack(this), "zaa", "aya", "aab",
                'y', Blocks.tnt,
        		'z', Blocks.planks,
        		'a', Items.paper,
        		'b', Items.string
    	);
	}
}
