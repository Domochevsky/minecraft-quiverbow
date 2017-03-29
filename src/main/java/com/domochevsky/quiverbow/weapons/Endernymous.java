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
import com.domochevsky.quiverbow.ammo.EnderQuartzClip;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.EnderAno;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Endernymous extends _WeaponBase
{
	public Endernymous()
	{
		super(8); 	// Max ammo placeholder

		ItemStack ammo = Helper.getAmmoStack(EnderQuartzClip.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}


	private String nameInternal = "Hidden Ender Pistol";
	private int MaxTicks;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderNymous");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderNymous_Empty");
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

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side, mob usable
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// SFX
		world.playSoundAtEntity(entity, "fireworks.largeBlast", 1.4F, 0.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 6, (byte) 4);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		this.fireShot(world, entity);	// Firing!

		if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
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

		EnderAno shot = new EnderAno(world, entity, (float) this.Speed);
		shot.damage = dmg;
		shot.ticksInAirMax = this.MaxTicks;

		world.spawnEntityInWorld(shot); 	// Firing
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(EnderQuartzClip.class, stack.getItemDamage());	// Unloading all ammo into that clip

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
			list.add(EnumChatFormatting.BLUE + "Ender Quartz: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Ender Quartz: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(EnumChatFormatting.GREEN + "Anonymous.");
		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use to drop the");
		list.add(EnumChatFormatting.YELLOW + "current clip.");
		list.add(EnumChatFormatting.YELLOW + "Craft with an Ender Quartz");
		list.add(EnumChatFormatting.YELLOW + "Clip to reload.");
		list.add("A weapon for those desiring");
		list.add("to stay unknown.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 16)", 16).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 24)", 24).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 5.0 BPT (Blocks Per Tick))", 5.0).getDouble();
		this.MaxTicks = config.get(this.nameInternal, "How long does my projectile exist, tops? (default 40 ticks)", 40).getInt();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "e e", "ofo", "oto",
					'o', Blocks.obsidian,
					'e', Blocks.end_stone,
					't', Blocks.tripwire_hook,
					'f', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(EnderQuartzClip.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (this.getCooldown(stack) > 0) { return "EnderNymous_hot"; }	// Cooling down
		return "EnderNymous";
	}
}
