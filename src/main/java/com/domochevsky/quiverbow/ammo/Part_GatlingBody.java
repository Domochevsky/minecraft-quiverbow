package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class Part_GatlingBody extends _AmmoBase
{	
	public Part_GatlingBody()
	{
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMaterials);	// On the combat tab by default, but this isn't ammo. It's a building part
	}
	
	
	@Override
	String getIconPath() { return "Gatling_Body"; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add("To be outfitted with 4 barrels.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) { return "Sugar Engine Main Body"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// Flavor as "over-indulgent" on pistons
        
        // Sugar Gatling, main body
        GameRegistry.addRecipe(new ItemStack(this), "rir", "ror", "tpb",
                'o', Blocks.obsidian,
                'i', Items.iron_ingot,
                't', Blocks.tripwire_hook,
                'r', Items.repeater,
        		'p', Blocks.planks,
        		'b', Blocks.piston
    	);
	}
}
