package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderQuartzClip extends _AmmoBase
{
	public EnderQuartzClip()
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		
		this.setMaxDamage(8);
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
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/EnderAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/EnderAmmo_Empty");
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
		if (world.isRemote) { return stack; }				// Not doing this on client side
		if (stack.getItemDamage() == 0) { return stack; }	// Already fully loaded
		
		if (player.inventory.hasItem(Items.ender_pearl) && player.inventory.hasItem(Items.quartz))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			
			player.inventory.consumeInventoryItem(Items.ender_pearl);	// We're just grabbing what we need from the inventory
			player.inventory.consumeInventoryItem(Items.quartz);
			
			// SFX
			world.playSoundAtEntity(player, "random.wood_click", 0.5F, 0.3F);
		}
		// else, doesn't have what it takes
		
		return stack;
    }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Ender Quartz: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(EnumChatFormatting.YELLOW + "Use magazine to fill it with");
		list.add(EnumChatFormatting.YELLOW + "Ender Pearls and Quartz.");
		list.add("A clip full of");
		list.add("quartz-encased ender pearls.");
		
		if (!player.inventory.hasItem(Items.ender_pearl)) { list.add(EnumChatFormatting.RED + "You don't have ender pearls."); }
		if (!player.inventory.hasItem(Items.quartz)) { list.add(EnumChatFormatting.RED + "You don't have quartz."); }
		if (player.capabilities.isCreativeMode) { list.add(EnumChatFormatting.RED + "Does not work in creative mode."); }
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Ender Quartz Clip"; }
	
	
	@Override
	public void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "xxx", "ixi", "iii",
		         'x', Items.quartz, 
		         'i', Items.iron_ingot
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
