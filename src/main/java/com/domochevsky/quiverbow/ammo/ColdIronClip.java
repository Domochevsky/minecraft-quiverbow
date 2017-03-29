package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class ColdIronClip extends _AmmoBase
{	
	@Override
	String getIconPath() { return "Bundle_Frost"; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add("Holds 4 ice-laced iron ingots."); 
		list.add("Cool to the touch."); 
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Cold Iron Clip"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A bundle of ice-laced iron ingots (4), merged with a slime ball
        GameRegistry.addShapelessRecipe(new ItemStack(this),
                Items.iron_ingot,
                Items.iron_ingot,
                Items.iron_ingot,
                Items.iron_ingot,
                Blocks.ice,
                Blocks.ice,
                Blocks.ice,
                Blocks.ice,
                Items.slime_ball
        );
	}
}
