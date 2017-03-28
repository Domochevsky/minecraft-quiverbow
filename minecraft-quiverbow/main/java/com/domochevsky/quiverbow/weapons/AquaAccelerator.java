package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.FillBucketEvent;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WaterShot;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AquaAccelerator extends _WeaponBase
{
	public AquaAccelerator() 
	{ 
		super(1); 
		this.setCreativeTab(CreativeTabs.tabTools);		// This is a tool
	}
	
	private String nameInternal = "Aqua Accelerator";
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{  
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/WaterGun_Empty");
	}
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
    {
		if (world.isRemote) { return stack; }				// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) 	// Is empty
		{ 
			this.checkReloadFromWater(stack, world, player);// See if you can reload
			return stack; 
		}	

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
    	
    	return stack;
    }
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet
		
		// SFX
		world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 2.0F);
		
		// Firing
		WaterShot projectile = new WaterShot(world, entity, (float) Speed);
		world.spawnEntityInWorld(projectile);
		
		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);	// Cooling down now
	}
	
	
	private void checkReloadFromWater(ItemStack stack, World world, EntityPlayer player)
    {
		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
		FillBucketEvent event = new FillBucketEvent(player, stack, world, movingobjectposition);
        
		if (MinecraftForge.EVENT_BUS.post(event)) { return; }
		
        MovingObjectPosition movObj = this.getMovingObjectPositionFromPlayer(world, player, true);

        if (movObj == null) { return; }	// Didn't click on anything in particular
        else
        {            
            if (movObj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int x = movObj.blockX;
                int y = movObj.blockY;
                int z = movObj.blockZ;

                if (!world.canMineBlock(player, x, y, z)) { return; }					// Not allowed to mine this, getting out of here
                if (!player.canPlayerEdit(x, y, z, movObj.sideHit, stack)) { return; }	// Not allowed to edit this, getting out of here

                Material material = world.getBlock(x, y, z).getMaterial();
                int meta = world.getBlockMetadata(x, y, z);

                // Is this water?
                if (material == Material.water && meta == 0)
                {
                	world.setBlockToAir(x, y, z);
                	stack.setItemDamage(0);
                	
                    return;
                }
                // else, not water
            }
            // else, didn't click on a block
        }
    }
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
	    super.addInformation(stack, player, list, par4);
	    
	    if (player.capabilities.isCreativeMode)
	    {
		    list.add(EnumChatFormatting.BLUE + "Buckets: INFINITE / " + this.getMaxDamage());
	    }
	    else
	    {
	    	int ammo = this.getMaxDamage() - this.getDamage(stack);
		    list.add(EnumChatFormatting.BLUE + "Buckets: " + ammo + " / " + this.getMaxDamage());
	    }
	    
	    list.add(EnumChatFormatting.YELLOW + "Craft with 1 Water Bucket to reload.");
	    list.add("Kinda slippery.");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean(true);
	}
    
	
	@Override
    public void addRecipes()
	{ 
		if (Enabled)
        {
			// One Aqua Accelerator (empty)
            GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ihi", "gpg", "iti",		
                   'p', Blocks.piston,
                   't', Blocks.tripwire_hook,
                   'i', Items.iron_ingot,
                   'h', Blocks.hopper,
                   'g', Blocks.glass_pane
            );
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
		
		// Fill the AA with one water bucket
        GameRegistry.addShapelessRecipe(new ItemStack(this),						
        		Items.water_bucket, 
        		new ItemStack(this, 1 , this.getMaxDamage())	// Empty
        );
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "AquaAcc_empty"; }	// empty
		
		return "AquaAcc";	// Regular
	}
}
