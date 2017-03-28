package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAccelerator;
import com.domochevsky.quiverbow.recipes.Recipe_ERA;
import com.domochevsky.quiverbow.recipes.Recipe_Weapon;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ERA extends _WeaponBase
{
	public ERA() { super(1); }
	
	private String nameInternal = "Ender Rail Accelerator";
	
	private double explosionSelf;
	public double explosionTarget;
	
	private boolean dmgTerrain;	// Can our projectile damage terrain?
	
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) 
	{ 
		if (stack.getItemDamage() == stack.getMaxDamage()) { return "Burnt Out " + this.namePublic; }
		return this.namePublic; 
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{  
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRailgun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRailgun_Empty");	// Burnt out
	}
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
	{	
		if (world.isRemote) { return stack; }	// Not doing this on client side
				
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return stack; }	// Is burnt out
		
		this.doSingleFire(stack, world, player);
		
		return stack;
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity) 
	{ 
		if (world.isRemote) { return; }	// Not doing this on client side
		
		if (this.isAccelerating(stack)) { return; }	// Already in the middle of firing
		
		// Firing
		this.startAccelerating(stack);
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 
	{
		if (world.isRemote) { return; }	// Not doing this on client side
		
		// Used for ticking up 27 * 2 times (with increasing pitch) after triggered and before firing
		// 54 ticks minimum per shot (movement in/out)
		
		if (this.isAccelerating(stack))
		{
			stack.getTagCompound().setInteger("acceleration", stack.getTagCompound().getInteger("acceleration") - 1);	// Ticking down
			stack.getTagCompound().setFloat("accSFX", stack.getTagCompound().getFloat("accSFX") + 0.02f);	// And pitching up
			
			world.playSoundAtEntity(entity, "mob.endermen.portal", stack.getTagCompound().getFloat("accSFX"), stack.getTagCompound().getFloat("accSFX"));
			// mob.endermen.portal
			// mob.enderdragon.wings
			
			if (stack.getTagCompound().getInteger("acceleration") <= 0) // Ready to fire
			{
				Helper.knockUserBack(entity, this.Kickback);			// Kickback
				
				// Upgrade
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 15.0f);	// Hurtin' (but less so)
				}
				else
				{
					entity.attackEntityFrom(DamageSource.causeThrownDamage(entity, entity), 20.0f);	// Hurtin'
				}
				
				boolean damageTerrain = world.getGameRules().getGameRuleBooleanValue("mobGriefing");
				
				if (!holdingItem)	// Isn't holding the weapon, so this is gonna go off in their pockets
				{
					entity.hurtResistantTime = 0;	// No rest for the wicked
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, (float) this.explosionTarget, this.dmgTerrain);	// Big baddaboom
					
					// Set weapon to "burnt out" (if the user's a player and not in creative mode)
					if (entity instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) entity;
						
						if (player.capabilities.isCreativeMode) {  }	// Is in creative mode, so not burning out
						else { stack.setItemDamage(1); }
					}
					// else, not a player. Not burning out
					
					return;	// We're done here
				}
				
				if (entity instanceof EntityPlayer) { damageTerrain = this.dmgTerrain; }	// Players don't care about mob griefing rules, but play by their own rules
				
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					// Has a muzzle, so no boom
					world.playSoundAtEntity(entity, "random.explode", 2.0F, 0.1F);
					NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 11, (byte) 6);
				}
				else
				{
					world.createExplosion(entity, entity.posX, entity.posY, entity.posZ, (float) this.explosionSelf, damageTerrain);	// Hurtin' more
				}				
				
				// Spawn projectile and go
				EnderAccelerator shot = new EnderAccelerator(world, entity, 5.0f);
				
				// Random Damage
				int dmg_range = DmgMax - DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
				int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
				dmg += DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)
				
				shot.damage = dmg;
				shot.ticksInAirMax = 120;	// 6 sec?
				shot.damageTerrain = damageTerrain;
				shot.explosionSize = (float) this.explosionTarget;
				
				world.spawnEntityInWorld(shot);
				
				// Set weapon to "burnt out" (if the user's a player and not in creative mode)
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer) entity;
					
					if (player.capabilities.isCreativeMode) {  }	// Is in creative mode, so not burning out
					else { stack.setItemDamage(1); }
				}
				// else, not a player. Not burning out
			}
			// else, not ready yet
		}
		// else, all's chill
	}
	
	
	private void startAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }
		
		stack.getTagCompound().setInteger("acceleration", 54);
		stack.getTagCompound().setFloat("accSFX", 0.02f);
	}
	
	
	private boolean isAccelerating(ItemStack stack)
	{
		if (stack.getTagCompound() == null) { return false; }
		
		if (stack.getTagCompound().getInteger("acceleration") <= 0) { return false; }	// If this is higher than 0 then it's currently counting down to the moment it fires
		
		return true;	// Seems to check out
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);
		
		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " to hit target.");
		
		list.add(EnumChatFormatting.GREEN + "Explosion with " + this.explosionTarget + " block radius on hit.");
		
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
		{
			list.add(EnumChatFormatting.GREEN + "Has an emerald muzzle.");
		}
		else
		{
			list.add(EnumChatFormatting.RED + "Explosion with " + this.explosionSelf + " block radius on firing.");
		}
		
		list.add(EnumChatFormatting.RED + "Burns out after one shot.");
		
		list.add("27 blocks worth of powered rail,");
		list.add("running through a single ender chest.");
		list.add("So not safe for use.");
	}
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		
		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing with a direct hit, at least? (default 120)", 120).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing with a direct hit, tops? (default 150)", 150).getInt();
		
		this.explosionSelf = config.get(this.nameInternal, "How big are my explosions when leaving the barrel? (default 4.0 blocks. TNT is 4.0 blocks)", 4.0).getDouble();
		this.explosionTarget = config.get(this.nameInternal, "How big are my explosions when hitting a target? (default 8.0 blocks. TNT is 4.0 blocks)", 8.0).getDouble();
		
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 30)", 30).getInt();
		
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);
		
		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. Too high-power and suicidal.)", false).getBoolean();
	}
	
	
	@Override
	public void addRecipes() 
	{
		if (Enabled) { this.registerRecipe(); }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
		
		// Reloading? More "repairing" the burnt out one

		this.registerRepair();
		this.registerUpgrade();
	}
	
	
	private void registerRecipe()
	{
		// Fully loaded
		// Alternate item registering method
		ItemStack[] input = new ItemStack[9];
	
		// Top row
		input[0] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		input[1] = new ItemStack(Blocks.golden_rail, 27);	// 27 rails
		input[2] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		
		// Middle row
		input[3] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		input[4] = new ItemStack(Item.getItemFromBlock(Blocks.ender_chest));
		input[5] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		
		// Bottom row
		input[6] = new ItemStack(Item.getItemFromBlock(Blocks.tripwire_hook));
		input[7] = new ItemStack(Items.iron_ingot);
		input[8] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		        
        GameRegistry.addRecipe(new Recipe_ERA(input, new ItemStack(this)));
	}
	
	
	private void registerRepair()
	{
		ItemStack[] repair = new ItemStack[9];
		
		// Top row
		//repair[0] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		repair[1] = new ItemStack(Blocks.golden_rail);
		//repair[2] = new ItemStack(Item.getItemFromBlock(Blocks.obsidian));
		
		// Middle row
		repair[3] = new ItemStack(Blocks.golden_rail);
		repair[4] = new ItemStack(this, 1 , this.getMaxDamage());
		repair[5] = new ItemStack(Blocks.golden_rail);
		
		// Bottom row
		repair[6] = new ItemStack(Items.redstone);
		repair[7] = new ItemStack(Items.iron_ingot);
		repair[8] = new ItemStack(Items.redstone);
		        
        GameRegistry.addRecipe(new Recipe_ERA(repair, new ItemStack(this)));
	}
	
	
	private void registerUpgrade()
	{
		ItemStack[] recipe = new ItemStack[9];
		
		// Top row
		recipe[0] = new ItemStack(Blocks.quartz_block);				// 0 1 2
		recipe[1] = new ItemStack(Items.emerald);					// - - -
		//recipe[2] = null;											// - - -
		
		// Middle row
		recipe[3] = new ItemStack(Blocks.emerald_block);			// - - -
		//recipe[4] = null;											// 3 4 5
		recipe[5] = new ItemStack(Items.emerald);					// - - -
		
		// Bottom row
		recipe[6] = new ItemStack(this);							// - - -
		recipe[7] = new ItemStack(Blocks.emerald_block);			// - - -
		recipe[8] = new ItemStack(Blocks.quartz_block);				// 6 7 8
		        
        GameRegistry.addRecipe(new Recipe_Weapon(recipe, new ItemStack(this), 1));	// Emerald Muzzle
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{ 
		if (this.getDamage(stack) == this.getMaxDamage()) { return "ERA_Empty"; }
		
		return "ERA";
	}
	
	
	@Override
	public EnumRarity getRarity(ItemStack stack)
    {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle")) { return EnumRarity.rare; }
        
        return EnumRarity.common;	// Default
    }
}
