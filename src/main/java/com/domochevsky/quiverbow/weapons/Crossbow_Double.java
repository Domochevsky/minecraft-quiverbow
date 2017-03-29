package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.RegularArrow;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Crossbow_Double extends _WeaponBase
{
	public Crossbow_Double() { super(2); }

	private String nameInternal = "Double Crossbow";


	// Icons
	@SideOnly(Side.CLIENT)
	public IIcon Icon_Half;

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Empty");
		this.Icon_Half = par1IconRegister.registerIcon("quiverchevsky:weapons/CrossbowDouble_Half");
	}


	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		if (this.getDamage(stack) >= this.getMaxDamage()) { return this.Icon_Empty; }	// Empty
		if (this.getDamage(stack) == 1) { return this.Icon_Half; }						// One arrow on the bay

		return this.Icon;
	}
	// This is for on-hand display. Only gets called on client side. Ideally won't get used at all once models are fully integrated


	@Override
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
		if (meta == this.getMaxDamage()) { return this.Icon_Empty; }	// Empty
		if (meta == 1) { return this.Icon_Half; }

		return this.Icon; 	// Full, default
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

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
		int dmg_range = this.DmgMax - this.DmgMin; 						// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;											// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II

		world.spawnEntityInWorld(entityarrow);	// pew

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		world.playSoundAtEntity(entity, "random.click", 0.5F, 0.4F);
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
		list.add(EnumChatFormatting.YELLOW + "Craft with 1 or 2 Arrows to reload.");
		list.add("A sticky piston powers the");
		list.add("reloading mechanism.");

		if (this.getCooldown(stack) != 0) {list.add(EnumChatFormatting.RED + "RE-TAUTING! (" + this.getCooldown(stack) + ")"); }
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 14)", 14).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 20)", 20).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();
		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 2)", 2).getInt();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 25 ticks)", 25).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One empty double crossbow (upgraded from regular crossbow)
			GameRegistry.addShapelessRecipe(new ItemStack(this, 1 , this.getMaxDamage()),
					Blocks.sticky_piston,
					Items.repeater,
					Helper.getWeaponStackByClass(Crossbow_Compact.class, true)
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty crossbow with two arrows
				Items.arrow,
				Items.arrow,
				new ItemStack(this, 1 , this.getMaxDamage())
				);

		GameRegistry.addShapelessRecipe(new ItemStack(this, 1, 1),	// Fill the empty crossbow with one arrow
				Items.arrow,
				new ItemStack(this, 1 , this.getMaxDamage())
				);

		GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the half empty crossbow with one arrow
				Items.arrow,
				new ItemStack(this, 1 , 1)
				);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() == 1) { return "CrossbowDouble_half"; }						// One arrow gone
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CrossbowDouble_empty"; }	// empty

		return "CrossbowDouble";	// Regular
	}
}
