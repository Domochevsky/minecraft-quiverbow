package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@cpw.mods.fml.common.Optional.Interface(modid = "battlegear2", iface = "mods.battlegear2.api.IUsableItem", striprefs = true)
public class _WeaponBase extends Item //implements mods.battlegear2.api.IUsableItem
{
	private String uniqueName = "WEAPON BASE";				// To be identified by
	private String namePublic = "QuiverBow Weapon Base";	// Default values
	private boolean Enabled;

	//private int weaponID;

	private int DmgMin;
	private int DmgMax;
	
	private double firingSpeed;

	private int Knockback;
	private byte Kickback;

	private int Cooldown;

	private boolean isMobUsable;	// Default

	// Icons
	@SideOnly(Side.CLIENT)
	public IIcon Icon;

	@SideOnly(Side.CLIENT)
	public IIcon Icon_Empty;


	public _WeaponBase(int maxAmmo)
	{
		this.setMaxStackSize(1);					// Default is 64
		this.setMaxDamage(maxAmmo);					// Default is 0
		this.setHasSubtypes(true);					// Got a subtype, since we're using damage values
		this.setFull3D();							// Not as thin as paper when held. Probably not relevant when using models
		this.setCreativeTab(CreativeTabs.tabCombat);// On the combat tab by default, since this is a weapon
	}


	public void setUniqueName(String name)
	{
		if (name == null || name.isEmpty()) { return; }

		this.uniqueName = name;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata. Only gets called on client side
	{
		if (meta == this.getMaxDamage()) { return this.Icon_Empty; }	// Empty
		return this.Icon; 	// Full, default
	}


	public int getMaxCooldown() { return this.Cooldown; }		// For QuiverMob (so they know how long to wait before trying again)
	public boolean isMobUsable() { return this.isMobUsable; }	// Usable by default


	// Removes the passed in value from the ammo stack
	// Returns true if the ammo has been used up
	boolean consumeAmmo(ItemStack stack, Entity entity, int ammo)
	{
		//if (!(entity instanceof EntityPlayer)) { return false; }	// Not a player, so not deducting ammo. Keep going!

		if (entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entity;
			if (player.capabilities.isCreativeMode) { return false; }	// Is in creative mode, so not changing ammo for them either. Keep going!
		}

		this.setDamage(stack, stack.getItemDamage() + ammo);

		if (stack.getItemDamage() >= this.getMaxDamage()) 	// All used up. This thing is now empty
		{
			this.setDamage(stack, this.getMaxDamage());	// Just making sure we're not going over the cap
			return true;
		}

		return false;	// There's still some left
	}


	void setCooldown(ItemStack stack, int cooldown)
	{
		if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }	// Init

		stack.getTagCompound().setInteger("cooldown", cooldown);	// Done
	}


	public int getCooldown(ItemStack stack)
	{
		if (stack == null) { return 0; }					// Why are you not holding anything?
		if (stack.getTagCompound() == null) { return 0; }	// No tag, no cooldown

		return stack.getTagCompound().getInteger("cooldown");	// Here ya go
	}


	void setBurstFire(ItemStack stack, int amount)	// Setting our burst fire to this amount. Assumes the tag to be valid
	{
		if (stack.getTagCompound() == null) { stack.setTagCompound(new NBTTagCompound()); }	// Init
		stack.stackTagCompound.setInteger("burstFireLeft", amount);
	}


	public int getBurstFire(ItemStack stack)
	{
		if (stack == null) { return 0; }			// Not a valid item
		if (!stack.hasTagCompound()) { return 0; }	// Doesn't have a tag

		return stack.stackTagCompound.getInteger("burstFireLeft");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	// Regular fire, as called by onItemRightClick. To be overridden by each individual weapon
	// Can also be called by mobs
	public void doSingleFire(ItemStack stack, World world, Entity entity) { }	// Server side


	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem) 	// Overhauled default
	{
		if (world.isRemote) { return; }	// Not doing this on client side

		if (this.getCooldown(stack) > 0) { this.setCooldown(stack, this.getCooldown(stack) - 1); }	// Cooling down
		if (this.getCooldown(stack) == 1) { this.doCooldownSFX(world, entity); }					// One tick before cooldown is done with, so SFX now
	}


	// Called one tick before cooldown is dealt with
	void doCooldownSFX(World world, Entity entity) { }


	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display


	@Override
	public double getDurabilityForDisplay(ItemStack stack) { return 1.0d / this.getMaxDamage() * this.getDamage(stack); }


	@Override
	public String getItemStackDisplayName(ItemStack stack) { return this.namePublic; }

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, this.getMaxDamage()));
	}
	

	public void addProps(FMLPreInitializationEvent event, Configuration config) { }
	public void addRecipes() { }
	
	
	public String getModelTexPath(ItemStack stack) { return null; }	// The model texture path


	protected String displayInSec(int tick) { return String.format("%.2f", tick * 0.05); }


	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) { return false; }


	@Override
	public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.bow; }


	@Override
	public int getMaxItemUseDuration(ItemStack stack) { return 40; }
	
	
	public String getUniqueName() { return this.uniqueName; }


	public void setFiringSpeed(int speed)
	{
		if (speed <= 0)
		{
			this.firingSpeed = 0.1d;
		}
		else
		{
			this.firingSpeed = speed;
		}
	}
	
	public double getFiringSpeed() { return this.firingSpeed; }
}
