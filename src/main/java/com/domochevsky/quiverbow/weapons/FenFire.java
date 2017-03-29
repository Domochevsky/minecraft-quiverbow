package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
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
import com.domochevsky.quiverbow.projectiles.FenGoop;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FenFire extends _WeaponBase
{
	public FenFire()
	{
		super(32);
		this.setCreativeTab(CreativeTabs.tabTools);		// Tool, so on the tool tab
	}

	private String nameInternal = "Fen Fire";

	private int FireDur;
	private int LightTick;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/FenFire");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/FenFire_Empty");
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

		// SFX
		world.playSoundAtEntity(entity, "random.bow", 0.7F, 0.3F);

		// Firing
		FenGoop projectile = new FenGoop(world, entity, (float) this.Speed);
		projectile.fireDuration = this.FireDur;

		if (this.LightTick != 0) { projectile.lightTick = this.LightTick; }	// Scheduled to turn off again

		world.spawnEntityInWorld(projectile);						// Firing!

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) { world.playSoundAtEntity(entity, "random.click", 0.8F, 2.0F); }


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Lights: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Lights: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.GREEN + "Fire for " + this.FireDur + " sec on hit.");
		list.add(EnumChatFormatting.GREEN + "Places glowstone on terrain hit.");

		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");	// 2 digits after the comma only

		list.add(EnumChatFormatting.YELLOW + "Craft with up to 8 Glowstone Blocks");
		list.add(EnumChatFormatting.YELLOW + "to reload.");

		list.add("It's emanating a warm light.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();
		this.FireDur = config.get(this.nameInternal, "How long is what I hit on fire? (default 1s)", 1).getInt();
		this.LightTick = config.get(this.nameInternal, "How long do my lights stay lit? (default 0 ticks for infinite. 20 ticks = 1 sec)", 0).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They despise light.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Fen Fire (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "di ", "i i", " ts",
					't', Blocks.tripwire_hook,
					'i', Items.iron_ingot,
					's', Blocks.sticky_piston,
					'd', Blocks.trapdoor
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Blocks.glowstone);

		Helper.makeAmmoRecipe(stack, 1, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 8, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 12, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 16, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 20, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 24, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 28, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 32, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "FenFire_empty"; }		// empty
		if (this.getCooldown(stack) > 0) { return "FenFire_hot"; }	// Cooling down

		return "FenFire";	// Regular
	}
}
