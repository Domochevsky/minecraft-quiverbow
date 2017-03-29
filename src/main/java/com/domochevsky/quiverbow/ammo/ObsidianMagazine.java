package com.domochevsky.quiverbow.ammo;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ObsidianMagazine extends _AmmoBase
{
	public ObsidianMagazine()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(16);
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/ObsidianAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/ObsidianAmmo_Empty");
	}

	
	@Override
    public IIcon getIconFromDamage(int meta) 
    {
		if (meta == this.getMaxDamage()) { return Icon_Empty; }
		
		return Icon;
    }
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
    {  
		if (world.isRemote) { return stack; }	// Not doing this on client side
		
		if (stack.getItemDamage() == 0) { return stack; }	// Already fully loaded
		
		boolean doSFX = false;
		
		Item obs = Item.getItemFromBlock(Blocks.obsidian);
		
		if (player.inventory.hasItem(Items.gunpowder) && player.inventory.hasItem(obs))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			
			player.inventory.consumeInventoryItem(Items.gunpowder);	// We're just grabbing what we need from the inventory
			player.inventory.consumeInventoryItem(obs);
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { world.playSoundAtEntity(player, "random.wood_click", 0.5F, 0.30F); }
		
		return stack;
    }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Splints: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		
		list.add(EnumChatFormatting.YELLOW + "Use magazine to fill it with");
		list.add(EnumChatFormatting.YELLOW + "Gunpowder and Obsidian.");
		
		list.add("A loading helper, full of");
		list.add("obsidian splints.");
		
		if (!player.inventory.hasItem(Items.gunpowder)) { list.add(EnumChatFormatting.RED + "You don't have gunpowder."); }
		if (!player.inventory.hasItem(Item.getItemFromBlock(Blocks.obsidian))) { list.add(EnumChatFormatting.RED + "You don't have obsidian."); }
		
		if (player.capabilities.isCreativeMode) { list.add(EnumChatFormatting.RED + "Does not work in creative mode."); }
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Obsidian Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xox",
		         'x', Items.iron_ingot, 
		         'o', Blocks.obsidian
		 );
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack( item, 1, this.getMaxDamage() ));
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display
}
