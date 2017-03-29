package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.projectiles.RegularArrow;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Crossbow_Auto extends _WeaponBase
{
	public Crossbow_Auto() { super(8); }

	private String nameInternal = "Auto-Crossbow";

	@SideOnly(Side.CLIENT)
	public IIcon Icon_Unchambered;	// Only relevant if you're using the non-model version

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)	// We got need for a non-typical icon currently. Will be phased out
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Empty");
		this.Icon_Unchambered = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowAuto_Unchambered");
	}


	@Override
	public IIcon getIcon(ItemStack stack, int pass)	// Onhand display
	{
		if (this.getDamage(stack) >= this.getMaxDamage()) { return this.Icon_Empty; }
		if (!this.getChambered(stack)) { return this.Icon_Unchambered; }	// Not chambered

		return this.Icon;
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		if (!this.getChambered(stack)) // No arrow on the rail
		{
			if (player.isSneaking()) { this.setChambered(stack, world, player, true); } // Setting up a new arrow

			return stack;	// Done here either way
		}

		if (player.isSneaking()) { return stack; }	// Still sneaking, even though you have an arrow on the rail? Not having it

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSoundAtEntity(entity, "random.bow", 1.0F, 0.5F);

		RegularArrow entityarrow = new RegularArrow(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II

		world.spawnEntityInWorld(entityarrow);	// pew

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
		this.setChambered(stack, world, entity, false);	// That bolt has left the rail
	}


	private boolean getChambered(ItemStack stack)
	{
		if (stack.stackTagCompound == null) { return false; }	// Doesn't have a tag

		return stack.stackTagCompound.getBoolean("isChambered");
	}


	private void setChambered(ItemStack stack, World world, Entity entity, boolean toggle)
	{
		if (stack.stackTagCompound == null) { stack.setTagCompound(new NBTTagCompound()); }	// Init

		stack.stackTagCompound.setBoolean("isChambered", toggle);	// Done, we're good to go again

		// SFX
		world.playSoundAtEntity(entity, "random.click", 0.8F, 0.5F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Bolts: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Bolts: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(EnumChatFormatting.GREEN + "Knockback " + this.Knockback + " on hit.");
		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use to ready a bolt.");
		list.add(EnumChatFormatting.YELLOW + "Craft with 1 Arrow Bundle to reload.");
		list.add("Pistons power the bolt feeder.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 10)", 10).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();
		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 1)", 1).getInt();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know how to rechamber me.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One auto-crossbow (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "iii", "pcp", " t ",
					'i', Items.iron_ingot,
					'p', Blocks.piston,
					't', Blocks.tripwire_hook,
					'c', Helper.getWeaponStackByClass(Crossbow_Double.class, true)
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu


		GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty auto-crossbow with one arrow bundle
				Helper.getAmmoStack(ArrowBundle.class, 0),
				new ItemStack(this, 1 , this.getMaxDamage())
				);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CrossbowAuto_empty"; }	// Empty
		if (!this.getChambered(stack)) { return "CrossbowAuto_unchambered"; }				// Not chambered

		return "CrossbowAuto";	// Regular
	}
}
