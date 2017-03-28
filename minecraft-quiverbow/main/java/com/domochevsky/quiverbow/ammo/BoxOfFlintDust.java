package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class BoxOfFlintDust extends _AmmoBase
{	
	public BoxOfFlintDust()
	{
		this.setMaxDamage(16);
		this.setCreativeTab(CreativeTabs.tabTools);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	@Override
	String getIconPath() { return "Bundle_Flint"; }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add("All boxed up.");
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Box of Flint Dust"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// A box of flint dust (4 dust per flint, meaning 32 per box), merged with wooden planks
        GameRegistry.addShapelessRecipe(new ItemStack(this),
                Items.flint,
                Items.flint,
                Items.flint,
                Items.flint,
                Items.flint,
                Items.flint,
                Items.flint,
                Items.flint,
                Blocks.planks
        ); 
	}
}
