package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.BlockEvent;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PowderKnuckle_Mod extends _WeaponBase
{
	public PowderKnuckle_Mod() { super(8); }

	private String nameInternal = "Modified Powder Knuckle";

	private double ExplosionSize;

	private boolean dmgTerrain;


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle_Modified");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/PowderKnuckle_Modified_Empty");
	}


	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sideX, float sideY, float sideZ)
	{
		if (world.isRemote) { return false; }	// Not doing this on client side

		// Right click
		if (this.getDamage(stack) >= this.getMaxDamage()) { return false; }	// Not loaded

		this.consumeAmmo(stack, player, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(world, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		// Dmg
		world.createExplosion(player, x, y, z, (float) this.ExplosionSize, true); 	// 4.0F is TNT

		// Mining
		for (int xAxis = -1; xAxis <= 1; xAxis++) // Along the x axis
		{
			for (int yAxis = -1; yAxis <= 1; yAxis++) // Along the y axis
			{
				for (int zAxis = -1; zAxis <= 1; zAxis++) // Along the z axis
				{
					this.doMining(world, (EntityPlayerMP) player, x + xAxis, y + yAxis, z + zAxis);	// That should give me 3 iterations of each axis on every level
				}
			}
		}

		return true;
	}


	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity)
	{
		if (player.worldObj.isRemote) { return false; }	// Not doing this on client side

		if (this.getDamage(stack) >= this.getMaxDamage())
		{
			entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMin);
			entity.hurtResistantTime = 0;	// No invincibility frames

			return false; 				// We're not loaded, getting out of here with minimal damage
		}

		this.consumeAmmo(stack, entity, 1);

		// SFX
		NetHelper.sendParticleMessageToAllPlayers(entity.worldObj, player.getEntityId(), (byte) 3, (byte) 4);	// smoke

		// Dmg
		entity.worldObj.createExplosion(player, entity.posX, entity.posY +0.5D, entity.posZ, (float) this.ExplosionSize, this.dmgTerrain); 	// 4.0F is TNT
		entity.setFire(2);																	// Setting fire to them for 2 sec, so pigs can drop cooked porkchops

		entity.attackEntityFrom(DamageSource.causePlayerDamage(player), this.DmgMax);	// Dealing damage directly. Screw weapon attributes

		return false;
	}


	void doMining(World world, EntityPlayerMP player, int x, int y, int z)	// Calling this 27 times, to blast mine a 3x3x3 area
	{
		Block toBeBroken = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);

		if (toBeBroken.getBlockHardness(world, x, y, z) == -1) { return; }	// Unbreakable

		if (toBeBroken.getHarvestLevel(meta) > 1) { return; }
		if (toBeBroken.getMaterial() == Material.water) { return; }
		if (toBeBroken.getMaterial() == Material.lava) { return; }
		if (toBeBroken.getMaterial() == Material.air) { return; }
		if (toBeBroken.getMaterial() == Material.portal) { return; }

		// Need to do checks here against invalid blocks
		if (toBeBroken == Blocks.water) { return; }
		if (toBeBroken == Blocks.flowing_water) { return; }
		if (toBeBroken == Blocks.lava) { return; }
		if (toBeBroken == Blocks.flowing_lava) { return; }
		if (toBeBroken == Blocks.obsidian) { return; }
		if (toBeBroken == Blocks.mob_spawner) { return; }

		// Crashing blocks: Redstone Lamp, Extended Piston
		// They're likely trying to drop things that cannot be dropped (active states of themselves)

		//WorldSettings.GameType gametype = WorldSettings.GameType.getByName("survival");
		WorldSettings.GameType gametype = world.getWorldInfo().getGameType();
		BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, gametype, player, x, y, z);

		if (event.isCanceled()) { return; }	// Not allowed to do this

		//toBeBroken.dropBlockAsItem(world, x, x, z, meta, 0);	// The last one is Fortune

		boolean removalSuccess = world.setBlockToAir(x, y, z);
		if (removalSuccess) { toBeBroken.onBlockDestroyedByPlayer(world, x, y, z, meta); }

		Item preBlockItem = toBeBroken.getItemDropped(meta, player.getRNG(), 0);

		if (preBlockItem == null) { return; }	// Item doesn't exist

		ItemStack blockItem = new ItemStack(preBlockItem);

		blockItem.setItemDamage(meta);

		EntityItem entityItem = new EntityItem(world, x, y + 0.5d, z, blockItem);
		entityItem.delayBeforeCanPickup = 10;

		world.spawnEntityInWorld(entityItem);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Gunpowder: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Gunpowder: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + (this.DmgMax + 1));

		list.add(EnumChatFormatting.GREEN + "Explosion with radius " + this.ExplosionSize + " on hit.");
		list.add(EnumChatFormatting.GREEN + "Mining 3x3x3 on use.");
		list.add(EnumChatFormatting.GREEN + "Silktouch 1 on use.");

		list.add(EnumChatFormatting.YELLOW + "Punch to attack mobs, Use to attack terrain.");
		list.add(EnumChatFormatting.YELLOW + "Craft with up to 8 gunpowder to reload.");

		list.add("Modified for blast mining.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What's my minimum damage, when I'm empty? (default 2)", 2).getInt();
		this.DmgMax = config.get(this.nameInternal, "What's my maximum damage when I explode? (default 14)", 14).getInt();

		this.ExplosionSize = config.get(this.nameInternal, "How big are my explosions? (default 1.5 blocks. TNT is 4.0 blocks)", 1.5).getDouble();
		this.dmgTerrain = config.get(this.nameInternal, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know where the trigger on this thing is.)", false).getBoolean(false);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// Modifying the powder knuckle once
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ooo", "oco", "i i",
					'c', Helper.getWeaponStackByClass(PowderKnuckle.class, true),
					'o', Blocks.obsidian,
					'i', Items.iron_ingot
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		ItemStack stack = new ItemStack(Items.gunpowder);

		Helper.makeAmmoRecipe(stack, 1, 1, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 2, 2, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 3, 3, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 4, 4, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 5, 5, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 6, 6, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 7, 7, this.getMaxDamage(), this);
		Helper.makeAmmoRecipe(stack, 8, 8, this.getMaxDamage(), this);
	}
}
