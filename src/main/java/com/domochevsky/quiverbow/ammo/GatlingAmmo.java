package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GatlingAmmo extends _AmmoBase
{
	public GatlingAmmo()
	{
		this.setMaxStackSize(1);
		this.setMaxDamage(200);
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	@Override
	String getIconPath() { return "GatlingAmmo"; }
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/GatlingAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/GatlingAmmo_Empty");
	}
	
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass)	// This is for on-hand display. The difference may be useful later
	{
		if (stack.getItemDamage() == this.getMaxDamage()) { return Icon_Empty; }
		
		return Icon;
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
		
		int counter = 4;
		
		while (counter > 0)	// Doing it 4 times, to speed that process up a bit
		{
			if (player.inventory.hasItem(Items.reeds) && player.inventory.hasItem(Items.stick))
			{
				// ...why does this not work? Is it because I'm in creative? Yes, it is. :|
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				
				//System.out.println("Set ITEM DMG to " + dmg + ".");
				
				player.inventory.consumeInventoryItem(Items.reeds);	// We're just grabbing what we need from the inventory
				player.inventory.consumeInventoryItem(Items.stick);
				
				// SFX
				doSFX = true;
			}
			// else, doesn't have what it takes
			else
			{
				//player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[" + this.getItemStackDisplayName(stack) + "] Can't find sticks or sugar canes."));
			}
			
			counter -= 1;
		}
		
		if (doSFX) { world.playSoundAtEntity(player, "random.wood_click", 0.5F, 1.50F); }
		
		return stack;
    }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Sugar Rods: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(EnumChatFormatting.YELLOW + "Use clip to fill it with Sugar");
		list.add(EnumChatFormatting.YELLOW + "Canes and Sticks.");
		list.add("A loading helper, full of");
		list.add("sugar cane-wrapped sticks.");
		
		if (!player.inventory.hasItem(Items.reeds))
		{
			list.add(EnumChatFormatting.RED + "You don't have sugar canes.");
		}
		if (!player.inventory.hasItem(Items.stick))
		{
			list.add(EnumChatFormatting.RED + "You don't have sticks.");
		}
		
		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.RED + "Does not work in creative mode.");
		}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Clip of Sugar Rods"; }
	
	
	@Override
	public void addRecipes() 
	{
		// First, the clip itself (empty)
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "y y", "y y", "yxy",
		         'x', Items.iron_ingot, 
		         'y', Blocks.planks
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
