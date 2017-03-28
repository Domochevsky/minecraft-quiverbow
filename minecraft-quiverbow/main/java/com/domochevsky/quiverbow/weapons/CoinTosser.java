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
import com.domochevsky.quiverbow.ammo.GoldMagazine;
import com.domochevsky.quiverbow.projectiles.CoinShot;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CoinTosser extends _WeaponBase
{
	private String nameInternal = "Coin Tosser";
	private boolean shouldDrop;


	public CoinTosser()
	{
		super(72); 	// Max ammo placeholder

		ItemStack ammo = Helper.getAmmoStack(GoldMagazine.class, 0);
		this.setMaxDamage(ammo.getMaxDamage());	// Fitting our max capacity to the magazine
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/CoinTosser");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/CoinTosser_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		//player.setItemInUse(stack, 60);	// Let's try that on both sides...

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
		world.playSoundAtEntity(entity, "random.break", 1.0F, 3.0F);

		this.setCooldown(stack, this.Cooldown);	// Cooling down now

		int counter = 0;

		while (counter < 9)	// Scatter 9
		{
			this.fireShot(world, entity, counter);	// Firing!

			if (this.consumeAmmo(stack, entity, 1)) 	// We're done here
			{
				this.dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.

			counter += 1;
		}
	}


	// Single firing action for something that fires multiple per trigger
	private void fireShot(World world, Entity entity, int shotCount)
	{
		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 				// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);	// Range will be between 0 and 10
		dmg += this.DmgMin;									// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		float spreadHor = 0;
		float spreadVert = 0;

		if (shotCount == 2)
		{
			spreadHor = 10.0f;
			spreadVert = 0f;
		}
		else if (shotCount == 3)
		{
			spreadHor = -10.0f;
			spreadVert = 0f;
		}
		else if (shotCount == 4)
		{
			spreadHor = 0.0f;
			spreadVert = -4.0f;
		}
		else if (shotCount == 5)
		{
			spreadHor = 0.0f;
			spreadVert = 2.0f;
		}
		else if (shotCount == 6)
		{
			spreadHor = 5.0f;
			spreadVert = 3.0f;
		}
		else if (shotCount == 7)
		{
			spreadHor = -5.0f;
			spreadVert = 3.0f;
		}
		else if (shotCount == 8)
		{
			spreadHor = 5.0f;
			spreadVert = 1.0f;
		}
		else if (shotCount == 9)
		{
			spreadHor = -5.0f;
			spreadVert = 1.0f;
		}

		CoinShot shot = new CoinShot(world, entity, (float) this.Speed, spreadHor, spreadVert);
		shot.damage = dmg;
		shot.setDrop(this.shouldDrop);

		world.spawnEntityInWorld(shot); 											// Firing
	}


	private void dropMagazine(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 60);
			return;
		}

		ItemStack clipStack = Helper.getAmmoStack(GoldMagazine.class, stack.getItemDamage());	// Unloading all ammo into that clip

		stack.setItemDamage(this.getMaxDamage());	// Emptying out

		// Creating the clip
		EntityItem entityitem = new EntityItem(world, entity.posX, entity.posY + 1.0d, entity.posZ, clipStack);
		entityitem.delayBeforeCanPickup = 10;

		// And dropping it
		if (entity.captureDrops) { entity.capturedDrops.add(entityitem); }
		else { world.spawnEntityInWorld(entityitem); }

		// SFX
		world.playSoundAtEntity(entity, "random.break", 1.0F, 0.5F);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Gold Nuggets: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Gold Nuggets: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax + " per Nugget");
		list.add(EnumChatFormatting.GREEN + "Scatter 9 when firing.");
		list.add(EnumChatFormatting.RED + "Cooldown for " + this.displayInSec(this.Cooldown) + " sec on use.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use to drop the current magazine.");
		list.add(EnumChatFormatting.YELLOW + "Craft with a Gold Magazine to reload.");
		list.add("Spring-loaded. It's pretty heavy.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing per nugget, at least? (default 1)", 1).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing per nugget, tops? (default 3)", 3).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();

		this.Kickback = (byte) config.get(this.nameInternal, "How hard do I kick the user back when firing? (default 1)", 1).getInt();

		this.Cooldown = config.get(this.nameInternal, "How long until I can fire again? (default 15 ticks)", 15).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);

		this.shouldDrop = config.get(this.nameInternal, "Do I drop gold nuggets on misses? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One coin tosser (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "z z", "zxz", " y ",
					'x', Blocks.piston,
					'y', Blocks.lever,
					'z', Items.iron_ingot
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Ammo
		Helper.registerAmmoRecipe(GoldMagazine.class, this);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "CoinTosser_empty"; }	// Empty
		if (this.getCooldown(stack) > 0) { return "CoinTosser_hot"; }

		return "CoinTosser";
	}
}
