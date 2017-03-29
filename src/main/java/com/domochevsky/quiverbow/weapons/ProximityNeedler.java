package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.NeedleMagazine;
import com.domochevsky.quiverbow.projectiles.ProxyThorn;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProximityNeedler extends _WeaponBase
{
	public ProximityNeedler()
	{
		super(64); 	// Max ammo placeholder

		ItemStack ammo = Helper.getAmmoStack(NeedleMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}


	private String nameInternal = "Proximity Thorn Thrower";
	private int MaxTicks;
	private int ProxyCheck;
	private int ThornAmount;
	private double triggerDist;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/ProxyNeedler");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/ProxyNeedler_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		if (player.isSneaking())	// Dropping the magazine
		{
			this.dropMagazine(world, stack, player);
			return stack;
		}

		if (this.getDamage(stack) >= this.getMaxDamage() - 7) { return stack; }	// Doesn't have enough ammo in it)

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// SFX
		world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 0.3F);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		this.fireShot(world, entity);	// Firing!

		if (this.consumeAmmo(stack, entity, 8)) 	// We're done here
		{
			this.dropMagazine(world, stack, entity);
		}
	}


	// Single firing action for something that fires multiple per trigger
	private void fireShot(World world, Entity entity)
	{
		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 1 and 10 (inclusive both)
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		ProxyThorn shot = new ProxyThorn(world, entity, (float) this.Speed);
		shot.damage = dmg;
		shot.ticksInGroundMax = this.MaxTicks;
		shot.triggerDistance = this.triggerDist;	// Distance in blocks

		shot.proxyDelay = this.ProxyCheck;
		shot.ThornAmount = this.ThornAmount;

		world.spawnEntityInWorld(shot); 	// Firing
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(NeedleMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntityInWorld(entityitem); }

		// SFX
		world.playSoundAtEntity(entity, "random.break", 1.0F, 0.3F);
	}


	@Override
	void doCooldownSFX(World world, Entity entity)
	{
		world.playSoundAtEntity(entity, "random.glass", 0.3F, 0.3F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Thorns: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Thorns: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per thorn");

		list.add(EnumChatFormatting.GREEN + "Scatters thorn splitters");
		list.add(EnumChatFormatting.GREEN + "on proximity trigger.");
		list.add(EnumChatFormatting.GREEN + "Trigger Range: " + this.triggerDist + " blocks");
		list.add(EnumChatFormatting.GREEN + "Projectile duration: " + this.displayInSec(this.MaxTicks) + " sec");

		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.RED + "Uses 8 thorns per shot.");

		list.add(EnumChatFormatting.YELLOW + "Crouch-use to drop the");
		list.add(EnumChatFormatting.YELLOW + "current clip.");
		list.add(EnumChatFormatting.YELLOW + "Craft with a Thorn");
		list.add(EnumChatFormatting.YELLOW + "Magazine to reload.");

		list.add("A bundle of pain, lying in wait.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing per thorn, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing per thorn, tops? (default 2)", 2).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0).getDouble();
		this.MaxTicks = config.get(this.nameInternal, "How long do my projectiles stick around, tops? (default 6000 ticks. That's 5 min.)", 6000).getInt();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 2)", 2).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();
		this.ProxyCheck = config.get(this.nameInternal, "How long does my projectile wait inbetween each proximity check? (default 20 ticks)", 20).getInt();
		this.ThornAmount = config.get(this.nameInternal, "How many thorns does my projectile burst into? (default 32)", 32).getInt();
		this.triggerDist = config.get(this.nameInternal, "What is the trigger distance of my projectiles? (default 2.0 blocks)", 2.0).getDouble();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean();
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ihi", "bpb", "tsi",
					't', Blocks.tripwire_hook,
					'b', Blocks.iron_bars,
					'i', Items.iron_ingot,
					'h', Blocks.hopper,
					's', Blocks.sticky_piston,
					'p', Blocks.piston
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(NeedleMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "PTT_empty"; }	// Cooling down

		return "PTT";
	}
}
