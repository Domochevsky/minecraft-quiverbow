package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.ammo.ObsidianMagazine;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.OSP_Shot;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OSP extends _WeaponBase
{
	public OSP()
	{
		super(16);
		ItemStack ammo = Helper.getAmmoStack(ObsidianMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage() * 2);	// Fitting our max capacity to the magazine. EDIT: Experimental double capacity
	}


	private String nameInternal = "Obsidian Splinter Pistol";

	private int Wither_Duration;	// 20 ticks to a second, let's start with 3 seconds
	private int Wither_Strength;	// 2 dmg per second for 3 seconds = 6 dmg total


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/OSP");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/OSP_Empty");
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
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSoundAtEntity(entity, "random.explode", 0.4F, 1.5F);
		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 3, (byte) 1);	// smoke

		// Firing
		OSP_Shot shot = new OSP_Shot(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		shot.damage = dmg;

		ShotPotion effect1 = new ShotPotion();

		effect1.potion = Potion.wither;
		effect1.Strength = this.Wither_Strength;
		effect1.Duration = this.Wither_Duration;

		shot.pot1 = effect1;

		world.spawnEntityInWorld(shot); 	// Firing!

		this.setCooldown(stack, this.Cooldown);
		if (this.consumeAmmo(stack, entity, 1)) { this.dropMagazine(world, stack, entity); }
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(ObsidianMagazine.class, MathHelper.floor_double(stack.getItemDamage() / 2) + 1);	// Unloading all ammo into that clip, with some loss

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntityInWorld(entityitem); }

		// SFX
		world.playSoundAtEntity(entity, "random.click", 1.7F, 0.3F);
	}


	@Override
	void doCooldownSFX(World world, Entity entity)
	{
		world.playSoundAtEntity(entity, "tile.piston.out", 0.3F, 0.4F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Small Splints: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Small Splints: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);

		list.add(EnumChatFormatting.GREEN + "Wither " + this.Wither_Strength + " for " + this.displayInSec(this.Wither_Duration) + " sec on hit.");

		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(EnumChatFormatting.YELLOW + "Crouch-use to drop the current magazine.");
		list.add(EnumChatFormatting.YELLOW + "Craft with 1 Obsidian Magazine to");
		list.add(EnumChatFormatting.YELLOW + "reload when empty.");

		list.add("The barrel's inside is blackened.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 8)", 8).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.7 BPT (Blocks Per Tick))", 1.7).getDouble();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.Wither_Strength = config.get(this.nameInternal, "How strong is my Wither effect? (default 1)", 1).getInt();
		this.Wither_Duration = config.get(this.nameInternal, "How long does my Wither effect last? (default 61 ticks)", 61).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Obsidian Splinter
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), " io", "ipi", "oft",
					'o', Blocks.obsidian,
					'i', Items.iron_ingot,
					'p', Blocks.piston,
					'f', Items.flint_and_steel,
					't', Blocks.tripwire_hook
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Reloading with obsidian magazine, setting its ammo metadata as ours (Need to be empty for that)
		Helper.registerAmmoRecipe(ObsidianMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "OSP_empty"; }	// empty
		if (this.getCooldown(stack) > 0) { return "OSP_hot"; }						// Cooling down

		return "OSP";	// Regular
	}
}
