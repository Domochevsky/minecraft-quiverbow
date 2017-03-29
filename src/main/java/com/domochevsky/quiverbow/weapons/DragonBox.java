package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.RocketBundle;
import com.domochevsky.quiverbow.projectiles.SmallRocket;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DragonBox extends _WeaponBase
{
	public DragonBox() { super(64); }

	private String nameInternal = "Dragon Box";

	private int FireDur;
	private double ExplosionSize;

	private boolean dmgTerrain;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Dragonbox");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Dragonbox_Empty");
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

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 		// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;								// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		// SFX
		world.playSoundAtEntity(entity, "fireworks.launch", 1.0F, 1.0F);

		// Firing
		SmallRocket shot = new SmallRocket(world, entity, (float) this.Speed, 0, 0);

		shot.damage = dmg;
		shot.fireDuration = this.FireDur;
		shot.explosionSize = this.ExplosionSize;
		shot.dmgTerrain = this.dmgTerrain;

		world.spawnEntityInWorld(shot);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
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

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(EnumChatFormatting.GREEN + "Fire for " + this.FireDur + " sec on hit");
		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.YELLOW + "Craft with up to 8 Firework");
		list.add(EnumChatFormatting.YELLOW + "Bundles to reload.");
		list.add("Crank-powered. The metal is bent.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 4)", 4).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 6)", 6).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3).getDouble();

		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 2)", 2).getInt();
		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 10 ticks)", 10).getInt();

		this.FireDur = config.get(this.nameInternal, "How long is what I hit on fire? (default 6s)", 6).getInt();

		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 1.0 blocks, for no terrain damage. TNT is 4.0 blocks)", 1.0).getDouble();
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One dragonbox (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxy", "azy", "zxy",
					'x', Items.stick,
					'y', Items.string,
					'z', Items.iron_ingot,
					'a', Items.flint_and_steel
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = Helper.getAmmoStack(RocketBundle.class, 0);

		Helper.makeAmmoRecipe(stack, 1, 8, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 16, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 24, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 32, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 40, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 48, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 56, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 64, this.getMaxDamage(), this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "Dragonbox_hot"; }	// Cooling down

		return "Dragonbox";	// Regular
	}
}
