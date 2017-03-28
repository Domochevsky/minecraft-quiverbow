package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.AI.AI_Properties;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Armor;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Communication;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Mobility;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Plating;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Riding;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Storage;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Weapon;

import cpw.mods.fml.common.registry.GameRegistry;


public class PackedUpAA extends _AmmoBase
{
	@Override
	String getIconPath() { return "WitherStand"; }
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Packed Up Arms Assistant"; }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Use to deploy.");
		list.add(EnumChatFormatting.YELLOW + "Use Assistant to give it your current weapon.");
		list.add(EnumChatFormatting.YELLOW + "Use Assistant to give it your current item.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use with empty hand to remove gear.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use empty Assistant to pack it up.");
		list.add(EnumChatFormatting.GREEN + "Use Iron Block on Assistant to repair it.");
		list.add(EnumChatFormatting.GREEN + "Use written book with one name/command per");
		list.add(EnumChatFormatting.GREEN + "line on Assistant to give it a list");
		list.add(EnumChatFormatting.GREEN + "of things not to shoot and to do.");
		list.add(EnumChatFormatting.RED + "Refer to manual for further instructions.");
		list.add("It's sizing you up.");
		
		if (stack.hasTagCompound())	// Customizations?
		{
			int maxHealth = 20;
			
			if (stack.getTagCompound().getBoolean("hasArmorUpgrade")) { maxHealth = 40; }	// Upgraded health, eh?
			
			list.add(EnumChatFormatting.BLUE + "Health: " + stack.getTagCompound().getInteger("currentHealth") + " / " + maxHealth);
			
			if (stack.getTagCompound().getBoolean("hasArmorUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has upgraded armor."); }
			if (stack.getTagCompound().getBoolean("hasHeavyPlatingUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has heavy plating."); }
			if (stack.getTagCompound().getBoolean("hasMobilityUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has mobile legs."); }
			if (stack.getTagCompound().getBoolean("hasStorageUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has extra storage."); }
			if (stack.getTagCompound().getBoolean("hasWeaponUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has a second rail."); }
			if (stack.getTagCompound().getBoolean("hasRidingUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has a seat."); }
			if (stack.getTagCompound().getBoolean("hasCommunicationUpgrade")) { list.add(EnumChatFormatting.BLUE + "Has a radio built in."); }
		}
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sideX, float sideY, float sideZ)
    {
		if (world.isRemote) { return true; }	// Not doing this on client side
		
		Entity_AA turret = new Entity_AA(world, player);
		
		turret.setPosition(x + 0.5, y +1 , z + 0.5);		
		world.spawnEntityInWorld(turret);
		
		// Custom name
		if (stack.hasDisplayName())	{ AI_Properties.applyNameTag(player, turret, stack, false); }
		
		// Applying upgrades
		if (stack.hasTagCompound())
		{
			if (stack.getTagCompound().getBoolean("hasArmorUpgrade")) { AI_Properties.applyArmorUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasHeavyPlatingUpgrade")) { AI_Properties.applyPlatingUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasMobilityUpgrade")) { AI_Properties.applyMobilityUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasStorageUpgrade")) { AI_Properties.applyStorageUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasWeaponUpgrade")) { AI_Properties.applyWeaponUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasRidingUpgrade")) { AI_Properties.applyRidingUpgrade(turret); }
			if (stack.getTagCompound().getBoolean("hasCommunicationUpgrade")) { AI_Properties.applyCommunicationUpgrade(turret); }
			
			if (stack.getTagCompound().getInteger("currentHealth") > 0)	// Tracking health and reapplying it
			{
				turret.setHealth(stack.getTagCompound().getInteger("currentHealth"));
			}
		}
		
		if (player.capabilities.isCreativeMode) { return true; }	// Not deducting them in creative mode
	
		stack.stackSize -= 1;
		if (stack.stackSize <= 0)	// Used up
		{
			player.setCurrentItemOrArmor(0, null);
		}
		
		return true;
    }
	
	
	@Override
	public void addRecipes() 
	{ 
		if (Main.allowTurret)
		{
			// Uses a Wither Skull and two eyes of ender
			// Plus a frame, legs, weapon rail...

			GameRegistry.addRecipe(new ItemStack(this), "ewe", "ibi", "ppp",	
	                'w', new ItemStack(Items.skull, 1, 1),	// Wither skull 
	                'e', Items.ender_eye,
	                'b', Blocks.iron_block,
	                'i', Items.iron_ingot,
	                'p', Blocks.sticky_piston
	        );
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not allowed to be on the creative tab either
		
		RecipeSorter.register("quiverchevsky:recipehandler_aa_armor", Recipe_AA_Armor.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_mobility", Recipe_AA_Mobility.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_storage", Recipe_AA_Storage.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_weapon", Recipe_AA_Weapon.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_riding", Recipe_AA_Riding.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_plating", Recipe_AA_Plating.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_aa_com", Recipe_AA_Communication.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
		// Upgrades
		this.registerArmorRecipe();
		this.registerPlatingRecipe();
		this.registerMobilityRecipe();
		this.registerStorageRecipe();
		this.registerSecondRailRecipe();
		this.registerRidingRecipe();
		this.registerCommunicationRecipe();
	}
	
	
	private void registerArmorRecipe()
	{
		ItemStack[] input = new ItemStack[2];
		
		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.diamond_block));	// 1 diamond block to upgrade this with
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasArmorUpgrade");
	}
	
	
	private void registerPlatingRecipe()
	{
		ItemStack[] input = new ItemStack[3];
		
		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.emerald_block));	// 1 diamond block to upgrade this with
		input[2] = new ItemStack(Item.getItemFromBlock(Blocks.iron_block));
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasHeavyPlatingUpgrade");
	}
	
	
	private void registerMobilityRecipe()
	{
		ItemStack[] input = new ItemStack[9];
		
		// Top
		input[0] = null;
		input[1] = new ItemStack(this);
		input[2] = null;
		
		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston));
		input[4] = new ItemStack(Items.iron_ingot);
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston));
		
		// Bottom
		input[6] = new ItemStack(Item.getItemFromBlock(Blocks.iron_bars));
		input[7] = null;
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.iron_bars));
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasMobilityUpgrade");
	}
	
	
	private void registerStorageRecipe()
	{
		ItemStack[] input = new ItemStack[9];
		
		// Top
		input[0] = null;
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.chest));
		input[2] = null;
		
		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston));
		input[4] = new ItemStack(this);
		input[5] = new ItemStack(Items.slime_ball);
		
		// Bottom
		input[6] = null;
		input[7] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		input[8] = null;
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasStorageUpgrade");
	}
	
	
	private void registerSecondRailRecipe()
	{
		ItemStack[] input = new ItemStack[9];
		
		// Top
		input[0] = null;
		input[1] = new ItemStack(this);
		input[2] = null;
		
		// Mid
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.iron_block));
		input[4] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston));
		
		// Bottom
		input[6] = null;
		input[7] = null;
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.sticky_piston));
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasWeaponUpgrade");
	}
	
	
	private void registerRidingRecipe()
	{
		ItemStack[] input = new ItemStack[3];
		
		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Items.saddle);
		input[2] = new ItemStack(Items.iron_ingot);
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasRidingUpgrade");
	}
	
	
	private void registerCommunicationRecipe()
	{
		ItemStack[] input = new ItemStack[3];
		
		input[0] = new ItemStack(this);
		input[1] = new ItemStack(Item.getItemFromBlock(Blocks.jukebox));	// Add a jukebox
		input[2] = new ItemStack(Items.repeater);							// And a repeater to operate it
		        
        Helper.registerAAUpgradeRecipe(new ItemStack(this), input, "hasCommunicationUpgrade");
	}
}
