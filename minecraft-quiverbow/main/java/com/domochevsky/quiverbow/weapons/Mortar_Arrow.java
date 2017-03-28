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
import com.domochevsky.quiverbow.ammo.ArrowBundle;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles.Sabot_Arrow;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Mortar_Arrow extends _WeaponBase
{
	public Mortar_Arrow() { super(8); }

	private String nameInternal = "Arrow Mortar";


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/ArrowMortar");
	}


	@Override
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
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
		// Good to go (already verified)
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 						// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;											// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// Firing
		Sabot_Arrow projectile = new Sabot_Arrow(world, entity, (float) this.Speed);
		projectile.damage = dmg;

		world.spawnEntityInWorld(projectile); 	// Firing!

		// SFX
		world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 2.0F);

		NetHelper.sendParticleMessageToAllPlayers(world, entity.getEntityId(), (byte) 11, (byte) 1);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	void doCooldownSFX(World world, Entity entity) // Server side
	{
		world.playSoundAtEntity(entity, "random.click", 0.6F, 2.0F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Arrow Bundles: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Arrow Bundles: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per arrow.");

		list.add(EnumChatFormatting.GREEN + "Scatter 8 on impact.");

		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");

		list.add(EnumChatFormatting.YELLOW + "Craft with up to 8 Arrow Bundles to reload.");

		list.add("Arrow tips are poking out of the barrel.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage are my arrows dealing, at least? (default 2)", 2).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage are my arrows dealing, tops? (default 10)", 10).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 3)", 3).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true.)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Arrow Mortar (Empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ipi", "isr", "tsr",
					't', Blocks.tripwire_hook,
					'i', Items.iron_ingot,
					's', Blocks.sticky_piston,
					'p', Blocks.piston,
					'r', Items.repeater
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack ammo = Helper.getAmmoStack(ArrowBundle.class, 0);

		Helper.makeAmmoRecipe(ammo, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(ammo, 8, 8, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "MortarArrow";	// Regular
	}
}
