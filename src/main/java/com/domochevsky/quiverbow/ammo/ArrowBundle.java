package com.domochevsky.quiverbow.ammo;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ArrowBundle extends _AmmoBase
{	
	@Override
	String getIconPath() { return "Bundle_Arrows"; }
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add("Holds 8 arrows, tightly packed.");
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Arrow Bundle"; }
	
	
	@Override
	public void addRecipes() 
	{ 
		// One arrow bundle, holding 8 arrows
		GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx",
                'x', Items.arrow,
                'y', Items.string
        );
		
		// Bundle of arrows back to 8 arrows
        GameRegistry.addShapelessRecipe(new ItemStack(Items.arrow, 8), new ItemStack(this) );
	}
}
