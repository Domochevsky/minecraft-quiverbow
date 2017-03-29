package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.AI.AI_Properties;
import com.domochevsky.quiverbow.FlyingAA.Entity_BB;

public class PackedUpBB extends _AmmoBase
{
	@Override
	String getIconPath() { return "PackedUpBB"; }
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Packed Up BB (WIP)"; }
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Use to deploy.");
	}
	
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sideX, float sideY, float sideZ)
    {
		if (world.isRemote) { return true; }	// Not doing this on client side
		
		Entity_BB turret = new Entity_BB(world, player);
		
		turret.setPosition(x + 0.5, y + 1 , z + 0.5);		
		world.spawnEntityInWorld(turret);
		
		// Custom name
		if (stack.hasDisplayName())	{ AI_Properties.applyNameTag(player, turret, stack, false); }
		
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
			
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not allowed to be on the creative tab either
		
	}
}
