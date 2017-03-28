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
import com.domochevsky.quiverbow.projectiles.BigRocket;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RPG extends _WeaponBase
{
	public RPG() { super(1); }

	private String nameInternal = "Fireworks Rocket Launcher";
	public double ExplosionSize;
	private int travelTime;	// How many ticks the rocket can travel before exploding
	private boolean dmgTerrain;	// Can our projectile damage terrain?


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG_Empty");
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
		BigRocket rocket = new BigRocket(world, entity, (float) this.Speed);	// Projectile Speed. Inaccuracy Hor/Vert
		rocket.explosionSize = this.ExplosionSize;
		rocket.travelTicksMax = this.travelTime;
		rocket.dmgTerrain = this.dmgTerrain;

		world.spawnEntityInWorld(rocket); 		// shoom.

		// SFX
		world.playSoundAtEntity(entity, "fireworks.launch", 2.0F, 0.6F);

		this.setCooldown(stack, 60);
		this.consumeAmmo(stack, entity, 1);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Rockets: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Rockets: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.GREEN + "Explosion with radius " + this.ExplosionSize + " on hit.");

		list.add(EnumChatFormatting.RED + "The rocket has a " + this.displayInSec(this.travelTime) + " sec fuse.");

		list.add("The fuse is way too short.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();
		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0).getDouble();
		this.travelTime = config.get(this.nameInternal, "How many ticks can my rocket fly before exploding? (default 20 ticks)", 20).getInt();
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Firework Rocket Launcher (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "x  ", "yx ", "zyx",
					'x', Blocks.planks,
					'y', Items.iron_ingot,
					'z', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Fill the RPG with 1 rocket
		GameRegistry.addRecipe(new ItemStack(this), " ab", "zya", " x ",
				'x', new ItemStack(this, 1 , this.getMaxDamage()),
				'y', Blocks.tnt,
				'z', Blocks.planks,
				'a', Items.paper,
				'b', Items.string
				);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "RPG_empty"; }	// Empty

		return "RPG";	// Regular
	}
}
