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

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.Seed;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Seedling extends _WeaponBase
{
	public Seedling() { super(32); }
	
	
	private String nameInternal = "Seedling";
	private int Dmg;
	private float Spread;
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Seedling");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Seedling_Empty");
	}
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }				// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) 	// Is empty. How does this still exist?
		{
			this.breakWeapon(world, stack, player);
			return stack;
		}
		
		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}
	
	
	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		// Good to go (already verified)
		
		world.playSoundAtEntity(entity, "random.click", 0.6F, 0.7F);
		
		float spreadHor = world.rand.nextFloat() * 10 - 5;	// Spread
		float spreadVert = world.rand.nextFloat() * 10 - 5;
		
		Seed shot = new Seed(world, entity, (float) this.Speed, spreadHor, spreadVert);
		shot.damage = this.Dmg;
		
		world.spawnEntityInWorld(shot); 	// Firing
		
		if (this.consumeAmmo(stack, entity, 1)) { this.breakWeapon(world, stack, entity); }
	}
	
	
	// All ammo has been used up, so breaking now
	private void breakWeapon(World world, ItemStack stack, Entity entity)
	{
		if (!(entity instanceof EntityPlayer)) // For QuiverMobs/Arms Assistants
		{
			this.setCooldown(stack, 40);
			return;
		}
		
		EntityPlayer player = (EntityPlayer) entity;
		
		player.renderBrokenItemStack(stack);
		player.destroyCurrentEquippedItem();	// Breaking
		stack.stackSize = 0;
		
		EntityItem piston = new EntityItem(world, player.posX, player.posY + 1.0F, player.posZ, new ItemStack(Blocks.piston));
		piston.delayBeforeCanPickup = 10;
		
		if (player.captureDrops) { player.capturedDrops.add(piston); }
		else { world.spawnEntityInWorld(piston); }
		
		EntityItem hook = new EntityItem(world, player.posX, player.posY + 1.0F, player.posZ, new ItemStack(Blocks.tripwire_hook));
		hook.delayBeforeCanPickup = 10;
		
		if (player.captureDrops) { player.capturedDrops.add(hook); }
		else { world.spawnEntityInWorld(hook); }
		
		world.playSoundAtEntity(player, "random.break", 1.0F, 1.5F);
	}
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);
		
		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Melon Seeds: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Melon Seeds: " + ammo + " / " + this.getMaxDamage());
		}
		
		list.add(EnumChatFormatting.BLUE + "Damage: " + this.Dmg);
		
		list.add(EnumChatFormatting.GREEN + "80% biologically degradable.");
		
		list.add(EnumChatFormatting.RED + "Cannot be reloaded.");
		
		list.add(EnumChatFormatting.YELLOW + "It's pre-loaded with 2 melons.");
		
		list.add("A small weapon made out of sugar cane.");
	}
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
		
		this.Dmg = config.get(this.nameInternal, "What damage am I dealing per projectile? (default 1)", 1).getInt();
		
		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 1.3 BPT (Blocks Per Tick))", 1.3).getDouble();
		
		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}
	
	
	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Seedling (fully loaded, meaning 0 damage)
			GameRegistry.addRecipe(new ItemStack(this, 1 , 0), "ada", "ada", "bca",
					'a', Items.reeds,
					'b', Blocks.tripwire_hook,
					'c', Blocks.piston,
					'd', Blocks.melon_block
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
	}
	
	
	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		return "Seedling";	// Regular
	}
}
