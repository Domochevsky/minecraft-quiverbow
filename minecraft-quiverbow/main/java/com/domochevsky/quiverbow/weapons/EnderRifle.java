package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderShot;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderRifle extends _WeaponBase
{
	public EnderRifle() { super(8); }


	private String nameInternal = "Ender Rifle";

	public int ZoomMax;
	private double DmgIncrease;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRifle");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderRifle_Empty");
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
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// Firing
		EnderShot shot = new EnderShot(world, entity, (float) this.Speed);		// Create the projectile

		shot.damage = this.DmgMin;
		shot.damage_Max = this.DmgMax;
		shot.damage_Increase = this.DmgIncrease;	// Increases damage each tick until the max has been reached

		shot.knockbackStrength = this.Knockback;

		world.spawnEntityInWorld(shot); 			// Pew.

		// SFX
		world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1);	// smoke

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled
	{
		if (world.isRemote) // Not doing this on client side
		{
			//ZoomInterface.checkClientZoom(world, entity, stack, this.ZoomMax);	// client zoom
			return;
		}

		if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
		if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now
	}


	@Override
	void doCooldownSFX(World world, Entity entity)
	{
		world.playSoundAtEntity(entity, "random.click", 0.7F, 0.2F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1);	// smoke
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Iron: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Iron: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(EnumChatFormatting.GREEN + "Knockback " + this.Knockback + " on hit.");
		list.add(EnumChatFormatting.GREEN + "Deals more damage the longer it travels.");
		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.YELLOW + "Crouch to zoom.");
		list.add(EnumChatFormatting.YELLOW + "Craft with up to 8 Iron Ingots to reload.");
		list.add("An ender-eye scope is attached.");
		list.add("It's staring at you.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 16)", 16).getInt();

		this.DmgIncrease = config.get(this.nameInternal, "By what amount does my damage rise? (default 1.0, for +1.0 DMG per tick of flight)", 1.0).getDouble();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 3.0 BPT (Blocks Per Tick))", 3.0).getDouble();

		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 1)", 1).getInt();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 25 ticks)", 25).getInt();

		this.ZoomMax = (config.get(this.nameInternal, "How far can I zoom in? (default 30. Less means more zoom)", 30).getInt());

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One ender rifle (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "aza", "bcy", "xzx",
					'x', Blocks.obsidian,
					'y', Blocks.tripwire_hook,
					'z', Items.iron_ingot,
					'a', Items.ender_eye,
					'b', Blocks.piston,
					'c', Blocks.sticky_piston
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Items.iron_ingot);

		Helper.makeAmmoRecipe(stack, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 8, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "EnderRifle_empty"; }
		if (this.getCooldown(stack) > 0) { return "EnderRifle_hot"; }	// Cooling down

		return "EnderRifle";	// Regular
	}
}
