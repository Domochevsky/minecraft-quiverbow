package com.domochevsky.quiverbow;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import com.domochevsky.quiverbow.ammo._AmmoBase;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.projectiles._ProjectileBase;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Armor;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Communication;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Mobility;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Plating;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Riding;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Storage;
import com.domochevsky.quiverbow.recipes.Recipe_AA_Weapon;
import com.domochevsky.quiverbow.recipes.Recipe_Ammo;
import com.domochevsky.quiverbow.weapons._WeaponBase;

import cpw.mods.fml.common.registry.GameRegistry;

public class Helper
{
	// Overhauled method for registering ammo (specifically, using magazines)
	public static void registerAmmoRecipe(Class<? extends _AmmoBase> ammoBase, Item weapon)
	{
		ArrayList list = new ArrayList();
		
		Item ammo = getAmmoByClass(ammoBase);
		
		ItemStack weaponStack = new ItemStack(weapon, 1, weapon.getMaxDamage());
		ItemStack ammoStack = new ItemStack(ammo);
		
		list.add(weaponStack);
		list.add(ammoStack);
		
		GameRegistry.addRecipe(new Recipe_Ammo(ammo, weapon, list));
	}
	
	
	public static void registerAAUpgradeRecipe(ItemStack result, ItemStack[] input, String upgradeType)
	{
		if (upgradeType.equals("hasArmorUpgrade"))
		{
			ArrayList list = new ArrayList();
			
			int counter = 0;
			
			while (counter < input.length)
			{
				list.add(input[counter]);
				
				counter += 1;
			}
			
			GameRegistry.addRecipe(new Recipe_AA_Armor(result, list));
		}
		
		
		else if (upgradeType.equals("hasHeavyPlatingUpgrade"))
		{
			ArrayList list = new ArrayList();
			
			int counter = 0;
			
			while (counter < input.length)
			{
				list.add(input[counter]);
				
				counter += 1;
			}
			
			GameRegistry.addRecipe(new Recipe_AA_Plating(result, list));
		}
		
		else if (upgradeType.equals("hasMobilityUpgrade"))
		{
			GameRegistry.addRecipe(new Recipe_AA_Mobility(3, 3, input, result));
		}
		
		else if (upgradeType.equals("hasStorageUpgrade"))
		{
			GameRegistry.addRecipe(new Recipe_AA_Storage(3, 3, input, result));
		}
		
		else if (upgradeType.equals("hasWeaponUpgrade"))
		{
			GameRegistry.addRecipe(new Recipe_AA_Weapon(3, 3, input, result));
		}
		
		else if (upgradeType.equals("hasRidingUpgrade"))
		{
			ArrayList list = new ArrayList();
			
			int counter = 0;
			
			while (counter < input.length)
			{
				list.add(input[counter]);
				
				counter += 1;
			}
			
			GameRegistry.addRecipe(new Recipe_AA_Riding(result, list));
		}
		
		else if (upgradeType.equals("hasCommunicationUpgrade"))
		{
			ArrayList list = new ArrayList();
			
			int counter = 0;
			
			while (counter < input.length)
			{
				list.add(input[counter]);
				
				counter += 1;
			}
			
			GameRegistry.addRecipe(new Recipe_AA_Communication(result, list));
		}
	}
	
	
	private static Item getAmmoByClass(Class<? extends _AmmoBase> ammoBase)
	{
		int counter = 0;
		
		while (counter < Main.ammo.length && Main.ammo[counter] != null)
		{
			if (Main.ammo[counter].getClass() == ammoBase) { return Main.ammo[counter]; }	// Found it
			
			counter += 1;
		}
		
		return null;	// Don't have what you're looking for
	}
	
	
	public static ItemStack getAmmoStack(Class<? extends _AmmoBase> ammoBase, int dmg)
	{
		int counter = 0;
		
		while (counter < Main.ammo.length && Main.ammo[counter] != null)
		{
			if (Main.ammo[counter].getClass() == ammoBase) { return new ItemStack(Main.ammo[counter], 1 , dmg); }
			
			counter += 1;
		}
		
		return null;	// No idea what you're looking for
	}
	
	
	public static ItemStack getWeaponStackByClass(Class<? extends _WeaponBase> weapon, boolean isEmpty)
	{
		int counter = 0;
		
		while (counter < Main.weapons.length && Main.weapons[counter] != null)
		{
			if (Main.weapons[counter].getClass() == weapon) // Found it
			{
				if (isEmpty) // They want the empty version of this thing
				{
					return new ItemStack(Main.weapons[counter], 1, Main.weapons[counter].getMaxDamage());
				}
				else
				{
					return new ItemStack(Main.weapons[counter]);
				}
			}
			
			counter += 1;
		}
		
		return null;	// No idea what you want
	}
	
	
	// ammo is the item to fill into the weapon. Only takes a single item type
	// ammoUse is how many items to fill in at a time
	// shotIncrease indicates how many shots we're loading into the weapon with each action
	// capacity is the maximum ammo capacity, starting at "empty" and counting down
	// Weapon is the weapon to use and produce out of this
	public static void makeAmmoRecipe(ItemStack ammo, int ammoUse, int shotIncrease, int capacity, Item weapon)
	{
		// Step 1, readying the array
		Object[] params = new Object[ammoUse + 1];	// Ammo, plus the weapon
		
		// Step 2, Filling that with the ammo
		int tempCount = 0;
		
		while (tempCount < ammoUse)	// if ammoCount is 4 then we'll count from 0 to 3, getting 4 items on there and being at 4 for the final weapon item
		{
			params[tempCount] = ammo;
			tempCount += 1;
		}
		
		// Step 3, making the recipes, for all possible loading states of this weapon
		
		int currentPos = capacity;	// The current loading state of this weapon
		
		while (currentPos >= shotIncrease)	// doing this until we've reached our last possible loading state
		{
			params[tempCount] = new ItemStack(weapon, 1, currentPos);	// The current weapon item with metadata to be reloaded
			GameRegistry.addShapelessRecipe(new ItemStack(weapon, 1, (currentPos - shotIncrease) ), params);
			// if this is 8 dmg (= empty) then reloading with this will be 5, all the way down to 0
			
			currentPos -= 1;	// Counting down until we reach 0 (= full)
		}
	}
	
	
	// Kicks the passed in entity backwards, relative to the passed in strength
	// Needs to be done both on client and server, because the server doesn't inform clients about small movement changes
	// This is the server-side part
	public static void knockUserBack(Entity user, byte strength)
	{
		user.motionZ += -MathHelper.cos((user.rotationYaw) * (float)Math.PI / 180.0F) * (strength * 0.08F);
		user.motionX += MathHelper.sin((user.rotationYaw) * (float)Math.PI / 180.0F) * (strength * 0.08F);
		
		NetHelper.sendKickbackMessage(user, strength);	// Informing the client about this
	}
	
	
	// Sets the projectile to be pickupable depending on player creative mode
	// Used for throwable entities
	public static void setThrownPickup(EntityLivingBase entity, _ProjectileBase shot)
	{
		if (entity instanceof EntityPlayer) 	// Is a player
		{
			// Creative mode?
			EntityPlayer player = (EntityPlayer) entity;
			
			if (player.capabilities.isCreativeMode) { shot.canBePickedUp = false; } // In creative mode, no drop
			else { shot.canBePickedUp = true; } 									// Not in creative, so dropping is permitted
			
		}
		else { shot.canBePickedUp = false; } // Not a player, so not dropping anything
	}
	
	
	// Unified appliance of potion effects
	public static void applyPotionEffect(EntityLivingBase entitylivingbase, ShotPotion pot)
	{
		if (entitylivingbase == null) { return; }	// Not a valid entity, for some reason
		
		if (pot == null) { return; }				// Nothing to apply
		
		PotionEffect potion = entitylivingbase.getActivePotionEffect(pot.potion);
		
		if (potion != null)	// Already exists. Extending it
		{
			int dur = potion.getDuration();
			
			entitylivingbase.addPotionEffect( new PotionEffect(pot.potion.id, pot.Duration + dur, pot.Strength - 1, false) );
		}
		else { entitylivingbase.addPotionEffect( new PotionEffect(pot.potion.id, pot.Duration, pot.Strength - 1, false) ); }	// Fresh
	}
	
	
	// Time to make a mess!
	// Checking if the block hit can be broken
	// stronger weapons can break more block types
	public static boolean tryBlockBreak(World world, Entity entity, MovingObjectPosition target, int strength)
	{
		if (!Main.breakGlass) { return false; }	// Not allowed to break anything in general
		
		if (entity instanceof _ProjectileBase)
		{
			_ProjectileBase projectile = (_ProjectileBase) entity;
			
			if (projectile.shootingEntity != null && !(projectile.shootingEntity instanceof EntityPlayer))
			{
				// Not shot by a player, so checking for mob griefing
				if (!world.getGameRules().getGameRuleBooleanValue("mobGriefing")) { return false; }	// Not allowed to break things
			}
		}
		
		Block block = world.getBlock(target.blockX, target.blockY, target.blockZ);
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		
		if (block == null) { return false; }	// Didn't hit a valid block? Do we continue? Stop?
		
		//float hardness = block.getBlockHardness(world, target.blockX, target.blockY, target.blockZ);	// 0.3 for glass
		
		boolean breakThis = false;

		if (strength >= 0)	// Weak stuff
		{
			if (block.getMaterial() == Material.cake)	// Hit something made of cake. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeCloth.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block.getMaterial() == Material.gourd)	// Hit something made of ...gourd? Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGrass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
		}
		
		if (strength >= 1)	// Medium stuff
		{
			if (block.getMaterial() == Material.glass)	// Hit something made of glass. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGlass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block.getMaterial() == Material.web)	// Hit something made of web. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeCloth.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block == Blocks.torch)	// Hit a torch. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeWood.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block == Blocks.pumpkin)	// Hit a pumpkin. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGrass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block == Blocks.melon_block)	// Hit a melon. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGrass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block == Blocks.flower_pot)	// Hit a flower pot. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGrass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
		}
		
		if (strength >= 2)	// Strong stuff
		{
			if (block.getMaterial() == Material.leaves)	// Hit something made of leaves. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGrass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
			
			else if (block.getMaterial() == Material.ice)	// Hit something made of ice. Breaking it!
			{
				world.playSoundAtEntity(entity, Block.soundTypeGlass.getBreakSound(), 1.0F, 1.0F);
				breakThis = true;
			}
		}
		
		if (strength >= 3)	// Super strong stuff
		{
			breakThis = true;	// Default breakage, then negating what doesn't work
			
			if (block.getMaterial() == Material.lava) { breakThis = false; }
			else if (block.getMaterial() == Material.air) { breakThis = false; }
			else if (block.getMaterial() == Material.portal) { breakThis = false; }
			
			else if (block == Blocks.bedrock) { breakThis = false; }
			else if (block == Blocks.lava) { breakThis = false; }
			else if (block == Blocks.flowing_lava) { breakThis = false; }
			else if (block == Blocks.obsidian) { breakThis = false; }
			else if (block == Blocks.mob_spawner) { breakThis = false; }
		}
		
		if (block == Blocks.beacon)	{ breakThis = false; }	// ...beacons are made out of glass, too. Not breaking those.
		
		if (breakThis)	// Breaking? Breaking!
		{
			if (Main.sendBlockBreak)
			{
				if (entity instanceof _ProjectileBase)
				{
					_ProjectileBase projectile = (_ProjectileBase) entity;

					// If you were shot by a player, are they allowed to break this block?
					Entity shooter = projectile.getShooter();

					if (shooter instanceof EntityPlayerMP)
					{
						WorldSettings.GameType gametype = world.getWorldInfo().getGameType();
						BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, gametype, (EntityPlayerMP) shooter, target.blockX, target.blockY, target.blockZ);

						if (event.isCanceled()) { return false; }	// Not allowed to do this
					}
				}
				else if (entity instanceof EntityPlayerMP)
				{
					WorldSettings.GameType gametype = entity.worldObj.getWorldInfo().getGameType();
					BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(entity.worldObj, gametype, (EntityPlayerMP) entity, target.blockX, target.blockY, target.blockZ);
					
					if (event.isCanceled()) { breakThis = false; }	// Not allowed to do this
				}
			}
			// else, not interested in sending such a event, so whatever

			world.setBlockToAir(target.blockX, target.blockY, target.blockZ);
			block.dropBlockAsItem(world, target.blockX, target.blockY, target.blockZ, meta, 0);
			
			return true;	// Successfully broken
		}
		
		return false;	// Couldn't break whatever's there
	}
	
	
	// Returns true if the asked for block has a valid material.
	// Used for attaching the fen light to blocks
	public static boolean hasValidMaterial(World world, int x, int y, int z)
	{
		Block block = world.getBlock(x, y, z);
		
		// Is the attached block a valid material?
		if (block.getMaterial() == Material.clay) { return true; }
		else if (block.getMaterial() == Material.cloth) { return true; }
		else if (block.getMaterial() == Material.grass) { return true; }
		else if (block.getMaterial() == Material.ground) { return true; }
		else if (block.getMaterial() == Material.iron) { return true; }
		else if (block.getMaterial() == Material.piston) { return true; }
		else if (block.getMaterial() == Material.rock) { return true; }
		else if (block.getMaterial() == Material.sand) { return true; }
		else if (block.getMaterial() == Material.wood) { return true; }
		else if (block.getMaterial() == Material.craftedSnow) { return true; }
		else if (block.getMaterial() == Material.leaves) { return true; }
		
		// No?
		return false;
	}
	
	
	// Does the weapon have a custom name or other upgrades? If so then we're transfering that to the new item
	public static void copyProps(IInventory craftMatrix, ItemStack newItem)
	{
		// Step 1, find the actual item (It's possible that this is not a reloading action, meaning there is no weapon to copy the name from)
		
		int slot = 0;
		
		while (slot < 9)
		{
			ItemStack stack = craftMatrix.getStackInSlot(slot);
			
			if (stack != null && stack.getItem() instanceof _WeaponBase)	// Found it. Does it have a name tag?
			{
				if (stack.hasDisplayName() && !newItem.hasDisplayName()) { newItem.setStackDisplayName(stack.getDisplayName()); }
				// else, has no custom display name or the new item already has one. Fine with me either way.
				
				// Upgrades
				if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("hasEmeraldMuzzle"))
				{
					if (!newItem.hasTagCompound()) { newItem.setTagCompound(new NBTTagCompound()); }	// Init
					newItem.getTagCompound().setBoolean("hasEmeraldMuzzle", true);						// Keeping the upgrade
				}
				
				return;	// Either way, we're done here
			}
			// else, either doesn't exist or not what I'm looking for
			
			slot += 1;
		}
	}
	
	
	public static boolean canEntityBeSeen(World world, Entity observer, Entity entity)
	{
		return rayTraceBlocks(world, Vec3.createVectorHelper(observer.posX, observer.posY + observer.getEyeHeight(), observer.posZ),
				Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)) == null;
	}
	
	
	private static MovingObjectPosition rayTraceBlocks(World world, Vec3 p_72933_1_, Vec3 p_72933_2_)
	{
		return getMovPosFromCollision(world, p_72933_1_, p_72933_2_, false, false, false);
	}
	
	
	private static MovingObjectPosition getMovPosFromCollision(World world, Vec3 observerPos, Vec3 targetPos, boolean p_147447_3_, boolean p_147447_4_, boolean p_147447_5_)
	{
		if (!Double.isNaN(observerPos.xCoord) && !Double.isNaN(observerPos.yCoord) && !Double.isNaN(observerPos.zCoord))
		{
			if (!Double.isNaN(targetPos.xCoord) && !Double.isNaN(targetPos.yCoord) && !Double.isNaN(targetPos.zCoord))
			{
				int targetPosX = MathHelper.floor_double(targetPos.xCoord);
				int targetPosY = MathHelper.floor_double(targetPos.yCoord);
				int targetPosZ = MathHelper.floor_double(targetPos.zCoord);
				
				int observerPosX = MathHelper.floor_double(observerPos.xCoord);
				int observerPosY = MathHelper.floor_double(observerPos.yCoord);
				int observerPosZ = MathHelper.floor_double(observerPos.zCoord);
				
				Block block = world.getBlock(observerPosX, observerPosY, observerPosZ);
				int blockMeta = world.getBlockMetadata(observerPosX, observerPosY, observerPosZ);

				boolean skip = false;
				
				if (block.getCollisionBoundingBoxFromPool(world, observerPosX, observerPosY, observerPosZ) == null) { skip = true; }	// Has no collision box
				else if (!block.canCollideCheck(blockMeta, p_147447_3_)) { skip = true; }						// Doesn't like being collided with
				else if (block.getMaterial() == Material.glass) { skip = true; }						// Is glass, which we can see through
				else if (block == Blocks.glass) { skip = true; }										// Is the actual glass block
				else if (block == Blocks.glass_pane) { skip = true; }									// Same for glass panes
				
				if (!skip)
				{
					MovingObjectPosition movingobjectposition = block.collisionRayTrace(world, observerPosX, observerPosY, observerPosZ, observerPos, targetPos);

					if (movingobjectposition != null) { return movingobjectposition; }
				}

				MovingObjectPosition movPos2 = null;
				blockMeta = 200;

				while (blockMeta-- >= 0)
				{
					if (Double.isNaN(observerPos.xCoord) || Double.isNaN(observerPos.yCoord) || Double.isNaN(observerPos.zCoord)) { return null; }

					if (observerPosX == targetPosX && observerPosY == targetPosY && observerPosZ == targetPosZ)
					{
						return p_147447_5_ ? movPos2 : null;
					}

					boolean flag6 = true;
					boolean flag3 = true;
					boolean flag4 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;

					if (targetPosX > observerPosX) { d0 = observerPosX + 1.0D; }
					else if (targetPosX < observerPosX) { d0 = observerPosX + 0.0D; }
					else { flag6 = false; }

					if (targetPosY > observerPosY) { d1 = observerPosY + 1.0D; }
					else if (targetPosY < observerPosY) { d1 = observerPosY + 0.0D; }
					else { flag3 = false; }

					if (targetPosZ > observerPosZ) { d2 = observerPosZ + 1.0D; }
					else if (targetPosZ < observerPosZ) { d2 = observerPosZ + 0.0D; }
					else { flag4 = false; }

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					
					double d6 = targetPos.xCoord - observerPos.xCoord;
					double d7 = targetPos.yCoord - observerPos.yCoord;
					double d8 = targetPos.zCoord - observerPos.zCoord;

					if (flag6) { d3 = (d0 - observerPos.xCoord) / d6; }
					if (flag3) { d4 = (d1 - observerPos.yCoord) / d7; }
					if (flag4) { d5 = (d2 - observerPos.zCoord) / d8; }

					boolean flag5 = false;
					byte sideHit;

					if (d3 < d4 && d3 < d5)
					{
						if (targetPosX > observerPosX) { sideHit = 4; }
						else { sideHit = 5; }

						observerPos.xCoord = d0;
						observerPos.yCoord += d7 * d3;
						observerPos.zCoord += d8 * d3;
					}
					else if (d4 < d5)
					{
						if (targetPosY > observerPosY) { sideHit = 0; }
						else { sideHit = 1; }

						observerPos.xCoord += d6 * d4;
						observerPos.yCoord = d1;
						observerPos.zCoord += d8 * d4;
					}
					else
					{
						if (targetPosZ > observerPosZ) { sideHit = 2; }
						else { sideHit = 3; }

						observerPos.xCoord += d6 * d5;
						observerPos.yCoord += d7 * d5;
						observerPos.zCoord = d2;
					}

					Vec3 vec32 = Vec3.createVectorHelper(observerPos.xCoord, observerPos.yCoord, observerPos.zCoord);
					observerPosX = (int)(vec32.xCoord = MathHelper.floor_double(observerPos.xCoord));

					if (sideHit == 5)
					{
						--observerPosX;
						++vec32.xCoord;
					}

					observerPosY = (int)(vec32.yCoord = MathHelper.floor_double(observerPos.yCoord));

					if (sideHit == 1)
					{
						--observerPosY;
						++vec32.yCoord;
					}

					observerPosZ = (int)(vec32.zCoord = MathHelper.floor_double(observerPos.zCoord));

					if (sideHit == 3)
					{
						--observerPosZ;
						++vec32.zCoord;
					}

					Block block1 = world.getBlock(observerPosX, observerPosY, observerPosZ);
					int block1meta = world.getBlockMetadata(observerPosX, observerPosY, observerPosZ);
					
					skip = false;
					
					if (block1.getCollisionBoundingBoxFromPool(world, observerPosX, observerPosY, observerPosZ) == null) { skip = true; }	// Has no collision box
					//else if (!block1.canCollideCheck(l1, p_147447_3_)) { skip = true; }						// Doesn't like being collided with
					else if (block1.getMaterial() == Material.glass) { skip = true; }						// Is glass, which we can see through
					else if (block1 == Blocks.glass) { skip = true; }										// Is the actual glass block
					else if (block1 == Blocks.glass_pane) { skip = true; }									// Same for glass panes

					if (!skip)
					{
						if (block1.canCollideCheck(block1meta, p_147447_3_))
						{
							MovingObjectPosition movPos1 = block1.collisionRayTrace(world, observerPosX, observerPosY, observerPosZ, observerPos, targetPos);

							if (movPos1 != null)
							{
								return movPos1;
							}
						}
						else
						{
							movPos2 = new MovingObjectPosition(observerPosX, observerPosY, observerPosZ, sideHit, observerPos, false);
						}
					}
				}

				return p_147447_5_ ? movPos2 : null;
			}
			else { return null; }
		}
		else { return null; }
	}
	
	
	@Deprecated
	public static boolean canSeeTarget(World world, Entity observer, Entity target)
	{
		if (target == null) { return false; }	// Can't see what doesn't exist
		if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isCreativeMode) { return false; }	// Shortcut: Never target creative mode players
		
		Vec3 observerPos = Vec3.createVectorHelper(observer.posX, observer.posY + observer.getEyeHeight(), observer.posZ);
		Vec3 targetPos = Vec3.createVectorHelper(target.posX, target.posY + target.getEyeHeight(), target.posZ);
		
		// Validation, it seems
		if (Double.isNaN(observerPos.xCoord)) { return false; }
		else if (Double.isNaN(observerPos.yCoord)) { return false; }
		else if (Double.isNaN(observerPos.zCoord)) { return false; }
		
		else if (Double.isNaN(targetPos.xCoord)) { return false; }
		else if (Double.isNaN(targetPos.yCoord)) { return false; }
		else if (Double.isNaN(targetPos.zCoord)) { return false; }
		
		int startPosX = MathHelper.floor_double(observerPos.xCoord);
		int startPosY = MathHelper.floor_double(observerPos.yCoord);
		int startPosZ = MathHelper.floor_double(observerPos.zCoord);
		
		int targetPosX = MathHelper.floor_double(targetPos.xCoord);
		int targetPosY = MathHelper.floor_double(targetPos.yCoord);
		int targetPosZ = MathHelper.floor_double(targetPos.zCoord);
		
		int currentPosX = startPosX;
		int currentPosY = startPosY;
		int currentPosZ = startPosZ;
		
		boolean hasReachedTarget = false;
		
		int blockCount = 0;
		
		Block currentBlock;
		int metadata;
		
		MovingObjectPosition movPos;
		
		System.out.println("[ARMS ASSISTANT] Checking line of sight against target -> " + target);
		
		// Only counting a certain number of blocks
		while (!hasReachedTarget && blockCount < 200)
		{
			currentBlock = world.getBlock(currentPosX, currentPosY, currentPosZ);
			metadata = world.getBlockMetadata(currentPosX, currentPosY, currentPosZ);
			
			boolean skip = false;	// Reset
			
			if (currentBlock.getCollisionBoundingBoxFromPool(world, currentPosX, currentPosY, currentPosZ) == null) { skip = true; }	// Has no collision box
			else if (!currentBlock.canCollideCheck(metadata, false)) { skip = true; }	// Doesn't like being collided with
			else if (currentBlock.getMaterial() == Material.glass) { skip = true; }		// Is glass, which we can see through
			else if (currentBlock == Blocks.glass) { skip = true; }						// Is the actual glass block
			else if (currentBlock == Blocks.glass_pane) { skip = true; }				// Same for glass panes
			
			if (!skip)	// Checks out
			{
				movPos = currentBlock.collisionRayTrace(
						world,
						currentPosX,
						currentPosY,
						currentPosZ,
						observerPos,
						targetPos
						);
				
				System.out.println("[ARMS ASSISTANT] Checking collision against block " + blockCount + " -> " + currentBlock.getUnlocalizedName() +
						" movPos is " + movPos + " -> X" + currentPosX + " / Y" + currentPosY + " / Z" + currentPosZ + " / Skip is " + skip + ".");
				
				if (movPos != null)
				{
					return false;	// We've hit a block
				}
			}
			
			// Have we reached the target position?
			if (currentPosX == targetPosX && currentPosY == targetPosY && currentPosZ == targetPosZ)
			{
				hasReachedTarget = true;	// We have arrived and not hit anything inbetween, so we're done here
			}
			
			// How do I get closer to the target now? currentPos needs to change, so the next cycle has fresh coords
			if (startPosX > targetPosX) { currentPosX -= 1; }
			else if (startPosX < targetPosX) { currentPosX += 1; }
			
			if (startPosY > targetPosY) { currentPosY -= 1; }
			else if (startPosY < targetPosY) { currentPosY += 1; }
			
			if (startPosZ > targetPosZ) { currentPosZ -= 1; }
			else if (startPosZ < targetPosZ) { currentPosZ += 1; }
			
			blockCount += 1;	// One more attempt made
		}
		
		
		return true;	// Here ya go
	}
}
