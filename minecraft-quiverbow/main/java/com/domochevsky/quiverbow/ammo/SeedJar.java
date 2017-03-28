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

public class SeedJar extends _AmmoBase
{
	public SeedJar()	// Holds seeds for the Seed Sweeper (512, for 8 per shot with 64 shots total), loaded directly into the weapon
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		
		this.setMaxDamage(512);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	@Override
	String getIconPath() { return "SeedJar"; }
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/SeedJar");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/SeedJar_Empty");
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
		
		int counter = 8;
		
		while (counter > 0)	// Doing it 8 times, to speed that process up a bit
		{
			boolean proceed = false;
			
			if (player.inventory.hasItem(Items.melon_seeds))
			{				
				player.inventory.consumeInventoryItem(Items.melon_seeds);
				proceed = true;
			}
			
			else if (player.inventory.hasItem(Items.pumpkin_seeds))
			{
				player.inventory.consumeInventoryItem(Items.pumpkin_seeds);
				proceed = true;
			}
			
			else if (player.inventory.hasItem(Items.wheat_seeds))
			{
				player.inventory.consumeInventoryItem(Items.wheat_seeds);
				proceed = true;
			}
			// else, doesn't have what it takes
			
			if (proceed)
			{
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				doSFX = true;
			}
			
			counter -= 1;
		}
		
		if (doSFX) { world.playSoundAtEntity(player, "random.wood_click", 0.6F, 0.7F); }
		
		return stack;
    }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Seeds: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		
		list.add(EnumChatFormatting.YELLOW + "Use jar to fill it with seeds.");
		
		if (player.inventory.hasItem(Items.melon_seeds)) { list.add(EnumChatFormatting.GREEN + "You have melon seeds."); }
		if (player.inventory.hasItem(Items.pumpkin_seeds)) { list.add(EnumChatFormatting.GREEN + "You have pumpkin seeds."); }
		if (player.inventory.hasItem(Items.wheat_seeds)) { list.add(EnumChatFormatting.GREEN + "You have wheat seeds."); }
		
		if (player.capabilities.isCreativeMode) { list.add(EnumChatFormatting.RED + "Does not work in creative mode."); }
		
		list.add("You could preserve apples with this, too.");
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) { return "Seed Jar"; }
	
	
	@Override
	public void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "gwg", "g g", "gig",
		         'g', Blocks.glass_pane, 
		         'i', Items.iron_ingot,
		         'w', Blocks.wooden_button
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
