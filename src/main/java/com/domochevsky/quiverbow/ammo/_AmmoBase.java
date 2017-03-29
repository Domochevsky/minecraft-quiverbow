package com.domochevsky.quiverbow.ammo;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class _AmmoBase extends Item
{	
	public _AmmoBase()
	{
		this.setMaxStackSize(16);
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
	}
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister)
	{ 
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/" + this.getIconPath());
	}
	
	@Override
    public IIcon getIconFromDamage(int meta) { return this.Icon; }
	
	
	String getIconPath() { return "Unknown"; }
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return false; }	// Don't care about durabilities
	
	
	public void addRecipes() { }	// Called once after all items have been registered and initialized
}
