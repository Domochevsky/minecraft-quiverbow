package com.domochevsky.quiverbow.ArmsAssistant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.AI.AI_Communication;
import com.domochevsky.quiverbow.AI.AI_Movement;
import com.domochevsky.quiverbow.AI.AI_Properties;
import com.domochevsky.quiverbow.AI.AI_RandomEquip;
import com.domochevsky.quiverbow.AI.AI_Storage;
import com.domochevsky.quiverbow.AI.AI_Targeting;
import com.domochevsky.quiverbow.AI.AI_WeaponHandler;
import com.domochevsky.quiverbow.AI.Commands;
import com.domochevsky.quiverbow.net.NetHelper;
import com.domochevsky.quiverbow.weapons.AA_Targeter;
import com.domochevsky.quiverbow.weapons._WeaponBase;

public class Entity_AA extends EntityLiving
{
	public double attackDistance = 16;	// In blocks, scanning in front of you every x ticks
	public double movementSpeed = 0.5d;	// Half speed. They're QUITE fast
	
	public ItemStack[] storage = new ItemStack[4];	// Stored items. Can be upgraded later

	public int movementDelay = 20;	// Delay in ticks between movement attempts
	
	public String ownerName = "Herobrine";	// Identifying the owner by display name. What could go wrong? :D
											// Default is for randomly spawned AAs
	
	public Entity currentTarget;	// Who we're aiming at (Shooting at whoever's closest, not the owner and targetable)
	public int targetDelay;			// Once a second?
	
	public boolean hasFirstWeapon;	// For restoration from NBT
	public _WeaponBase firstWeapon;		// The weapon interface
	public int firstAttackDelay = 60;		// Delay in ticks between firing attempts
	
	public boolean hasSecondWeapon;	// For restoration from NBT
	public _WeaponBase secondWeapon;	// The offhand weapon, if the upgrade is there
	public int secondAttackDelay = 60;
		
	public boolean hasArmorUpgrade;			// Gains double health with this
	public boolean hasMobilityUpgrade;		// Can move around (and follow the owner if so written in the book) if it has this
	public boolean hasStorageUpgrade;		// Can carry 8 instead of 4 items with this
	public boolean hasWeaponUpgrade;		// Can carry a second weapon
	public boolean hasRidingUpgrade;		// Can be ridden around by the owner
	public boolean hasHeavyPlatingUpgrade;	// Experimental. -3 Dmg reduction
	public boolean hasCommunicationUpgrade;	// Can be commanded with the AA Targeter
	
	private int armorPlatingDmgReduction = 3;
	
	public boolean wasRiddenLastTick;
	public boolean riddenThisTick;
	
	// Used by the mobility upgrade and the STAY command
	public double stationaryX;
	public double stationaryY;
	public double stationaryZ;
	
	private int idleTime;
	
	private int sendStateDelay = 1;		// First init
	private int sendInventoryDelay = 1;
	
	public boolean hasToldOwnerAboutFirstAmmo;	// To ensure that this is only send once
	public boolean hasToldOwnerAboutSecondAmmo;
	
	public boolean canFly;
	
	public double waypointX;
	public double waypointY;
	public double waypointZ;
	
	public int courseChangeCooldown;	// Doing it like ghasts
	

	public Entity_AA(World world) // Generic, for use on client side
	{
		super(world);
		this.renderDistanceWeight = 10.0d;
		
		this.height = 1.65f;
		this.boundingBox.setBounds(-0.5d, 0.0d, -0.5d, 0.5d, this.height, 0.5d);
		
		this.setCanPickUpLoot(false);
		
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementSpeed);
		
		//if (!world.isRemote) { AI_RandomEquip.setupGear(this); }	// Hand me my gear! 
	}
	
	
	public Entity_AA(World world, EntityPlayer player) 
	{
		super(world);
		
		this.renderDistanceWeight = 10.0d;
		
		if (player != null)
		{
			this.ownerName = player.getDisplayName();
			
			this.setPositionAndRotation(player.posX, player.posY, player.posZ, player.cameraYaw, player.cameraPitch);
			this.worldObj.playSoundAtEntity(this, "random.anvil_land", 0.7f, 1.5f);
		}
		else
		{
			AI_RandomEquip.setupGear(this);	// Hand me my gear!
		}
		
		this.height = 1.65f;
		this.boundingBox.setBounds(-0.5d, 0.0d, -0.5d, 0.5d, this.height, 0.5d);
		
		this.setCanPickUpLoot(false);
		
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementSpeed);
	}
	
	
	@Override
	public double getMountedYOffset()
    {
        return (double) this.height;
        // return (double) this.height * 0.75D;
    }
	
	
	@Override
	protected boolean isAIEnabled() { return true; }
	
	@Override
	public float getAIMoveSpeed() { return (float) this.movementSpeed; }
	
	
	@Override
	protected boolean canDespawn() { return false; }
	
	
	@Override
	protected void entityInit() { super.entityInit(); }
	
	
	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) { return this.boundingBox; }

	
	@Override
	public boolean canBreatheUnderwater() { return true; }
	
	
	@Override
    public void onLivingUpdate()
    {
		super.onLivingUpdate();
		
		if (this.worldObj.isRemote) { return; }	// Not doing this on client side
		
		this.riddenThisTick = this.hasRidingUpgrade && this.riddenByEntity != null;
		
		this.sendState();
		this.tickWeapons();
		this.useCommunication();
		
		this.updateEntityActionState();
		this.rotationYawHead = this.rotationYaw;
		
		AI_Targeting.targetNearestEntity(this);	// Target whoever's closest
		AI_Targeting.lookAtTarget(this, false);	// Eye them up
		
		if (!AI_Targeting.isNameOnWhitelist(this, Commands.cmdHoldFire))
		{
			this.fireWeapons(false);	// Fire if you can
		}
		
		if (this.hasMobilityUpgrade)
		{
			AI_Movement.handleMovement(this);
			this.sendPositionUpdate();
			//this.updateAITasks();	// For movement to actually happen
		}
		
		if (this.wasRiddenLastTick && !this.riddenThisTick)	// Not ridden anymore
		{
			if (AI_Targeting.isNameOnWhitelist(this, Commands.cmdStayStationary))	// We've been ridden to another place, so refreshing our coords now
			{
				this.stationaryX = this.posX;
				this.stationaryY = this.posY;
				this.stationaryZ = this.posZ;
			}
		}
		
		/*if (this.canFly)
		{
			if (!this.onGround && this.motionY < 0.0D)
	        {
	            this.motionY *= 0.2D;	// Slowfall
	        }
		}*/
		
		// Keeping track
		this.wasRiddenLastTick = this.riddenThisTick;
    }
	
	
	// Done in regular intervals so players nearby know what we look like
	private void sendState()
	{
		this.sendStateDelay -= 1;	// Ticking down
		this.sendInventoryDelay -= 1;
		
		if (this.sendStateDelay <= 0)
		{
			this.sendStateDelay = 100;	// 5 sec
			AI_Properties.sendStateToPlayersInRange(this);	// Update all nearby players
		}
		
		if (this.sendInventoryDelay <= 0)
		{
			this.sendInventoryDelay = 200;	// 10 sec
			AI_Properties.sendInventoryToPlayersInRange(this);	// Update all nearby players
		}
	}
	
	
	private void tickWeapons()
	{
		// Ticking down
		if (this.firstAttackDelay > 0) { this.firstAttackDelay -= 1; }
		if (this.secondAttackDelay > 0) { this.secondAttackDelay -= 1; }
		
		if (this.firstWeapon != null && this.getEquipmentInSlot(0) != null)
		{
			if (this.firstWeapon.getBurstFire(this.getHeldItem()) > 0)	// Doing burst fire right now. Adjust your viewing angle
			{
				this.faceTarget(this.currentTarget, 30f, 30f);
			}
			
			this.firstWeapon.onUpdate(this.getHeldItem(), this.worldObj, this, this.ticksExisted, true);	// For cooldown and burst
		}
		
		if (this.secondWeapon != null && this.getEquipmentInSlot(1) != null)
		{
			if (this.secondWeapon.getBurstFire(this.getEquipmentInSlot(1)) > 0)	// Doing burst fire right now. Adjust your viewing angle
			{
				this.faceTarget(this.currentTarget, 30f, 30f);
			}
			
			this.secondWeapon.onUpdate(this.getEquipmentInSlot(1), this.worldObj, this, this.ticksExisted, true);	// For cooldown and burst
		}
	}
	
	
	private void useCommunication()
	{
		if (!this.hasCommunicationUpgrade) { return; }	// Can't communicate with the owner
		
		if (AI_Targeting.isNameOnWhitelist(this, Commands.cmdFireRemote))	// Told to fire together with them, so doing that
		{
			EntityPlayer owner = this.worldObj.getPlayerEntityByName(this.ownerName);
			
			if (owner != null) 
			{ 
				if (owner.getHeldItem() != null && owner.getHeldItem().getItem() instanceof AA_Targeter)
				{
					AA_Targeter weapon = (AA_Targeter) owner.getHeldItem().getItem();
					MovingObjectPosition movPos = AI_Targeting.getMovingObjectPositionFromPlayer(this.worldObj, owner, weapon.targetingDistance);
					
					if (movPos != null)
					{
						//System.out.println("[AA] Check 3. movPos is " + movPos);
						
						if (movPos.entityHit != null)	// Hit an entity
						{
							AI_Targeting.lookAtTarget(this, movPos.entityHit, false);
						}
						else	// Hit a block
						{
							this.faceTargetBlock(movPos.blockX, movPos.blockY, movPos.blockZ);
						}
					}
					// else, not looking at anything tangible, hm?
					
					if (weapon.getCooldown(owner.getHeldItem()) > 0) 
					{
						this.fireWeapons(true); // Fire with reckless abandon!
					}
					// else, not cooling down, meaning not firing
				}
				// else, not holding the AA Targeter
			}
			// Else, might not be online right now
		}
	}
	
	
	private void fireWeapons(boolean ignoreValidation)
	{
		if (this.hasFirstWeapon && this.firstAttackDelay <= 0)	// First rail is ready
		{
			if (this.riddenThisTick && this.hasCommunicationUpgrade) 
			{
				AI_WeaponHandler.fireWithOwner(this, false); 
			}
			else { AI_WeaponHandler.attackTarget(this, false, ignoreValidation); }
		}	
		
		if (this.hasSecondWeapon && this.secondAttackDelay <= 0) // Second rail is ready
		{ 
			if (this.riddenThisTick && this.hasCommunicationUpgrade) 
			{
				AI_WeaponHandler.fireWithOwner(this, true);
			}
			else { AI_WeaponHandler.attackTarget(this, true, ignoreValidation); }
		}
	}
	
	
	public void faceTarget(Entity target, float speedYaw, float speedPitch)
    {
		if (target == null) { return; }	// Nothing to face
		
		double velX = target.posX - target.lastTickPosX;
		//double velZ = target.posZ - target.lastTickPosZ;
		double VelY = target.posY - target.lastTickPosY;
		
        double distanceX = (target.posX + velX) - this.posX;
        double distanceZ = target.posZ - this.posZ;
        double distanceY;

        if (target instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase) target;
            distanceY = entitylivingbase.posY + VelY + (double) entitylivingbase.getEyeHeight() - (this.posY + (double) this.getEyeHeight());
        }
        else
        {
            distanceY = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0D + VelY - (this.posY + (double) this.getEyeHeight());
        }

        double distanceSqr = (double)MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
        float targetYaw = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
        float targetPitch = (float)(-(Math.atan2(distanceY, distanceSqr) * 180.0D / Math.PI));
       
        this.rotationPitch = this.updateRotation(this.rotationPitch, targetPitch, speedPitch);
        this.rotationYaw = this.updateRotation(this.rotationYaw, targetYaw, speedYaw);
    }
	
	
	public void faceTargetBlock(double posX, double posY, double posZ)
    {		
        double distanceX = posX - this.posX;
        double distanceZ = posZ - this.posZ;
        double distanceY = posY - this.posY;

        double distanceSqr = (double)MathHelper.sqrt_double(distanceX * distanceX + distanceZ * distanceZ);
        float targetYaw = (float)(Math.atan2(distanceZ, distanceX) * 180.0D / Math.PI) - 90.0F;
        float targetPitch = (float)(-(Math.atan2(distanceY, distanceSqr) * 180.0D / Math.PI));
       
        this.rotationPitch = this.updateRotation(this.rotationPitch, targetPitch, 30.0f);
        this.rotationYaw = this.updateRotation(this.rotationYaw, targetYaw, 30.f);
        
        AI_Targeting.adjustAim(this, false, posX, posY, posZ);	// Aim adjustment
    }
	
	
	public float updateRotation(float p_70663_1_, float p_70663_2_, float speed)
    {
        float f3 = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);

        if (f3 > speed) { f3 = speed; }
        if (f3 < -speed) { f3 = -speed; }

        return p_70663_1_ + f3;
    }
	
	
	@Override
	protected void updateEntityActionState()
    {
		if (this.hasRidingUpgrade && this.riddenByEntity != null) // Not looking around idle, since we're being ridden
		{ 
			EntityLivingBase rider = (EntityLivingBase) this.riddenByEntity;
			
			// Look where the rider is looking
			this.rotationPitch = this.updateRotation(this.rotationPitch, rider.rotationPitch, 30.0f);
			this.rotationYaw = this.updateRotation(this.rotationYaw, rider.rotationYaw, 30.0f);
			
			return; // We're done here
		}	
		
        super.updateEntityActionState();
        this.moveStrafing = 0.0F;
        this.moveForward = 0.0F;
        float f = 8.0F;

        if (this.rand.nextFloat() < 0.02F) { this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F; }

        if (this.rand.nextFloat() < 0.05F) { this.randomYawVelocity = (this.rand.nextFloat() - 0.5F) * 20.0F; }

        this.rotationYaw += this.randomYawVelocity;
        this.rotationPitch = this.defaultPitch;

        if (this.isInWater() || this.handleLavaMovement()) { this.isJumping = this.rand.nextFloat() < 0.8F; }
    }
	
	
	private void lookIdle()
	{
		if (this.getRNG().nextFloat() >= 0.02F) { return; }	// Not yet
		if (this.idleTime >= 0) { return; }					// Still ticking down
		
		this.idleTime -= 1;	// Ticking down
		
		double d0 = (Math.PI * 2D) * this.getRNG().nextDouble();
        double lookX = Math.cos(d0);
        double lookZ = Math.sin(d0);
        
        this.idleTime = 20 + this.getRNG().nextInt(20);
	}
	
	
	protected void sendPositionUpdate()
	{
		if (this.posX == this.lastTickPosX && this.posY == this.lastTickPosY && this.posZ == this.lastTickPosZ) { return; }	// Still in the same position
		
		NetHelper.sendPositionMessageToPlayersInRange(this.worldObj, this, this.posX, this.posY, this.posZ);	// We are mobile, so keeping players informed about our position
	}
	
	
	@Override
	public boolean interact(EntityPlayer player)
    {
		if (this.worldObj.isRemote) { return true; }	// Client side. Doesn't have the same info, so makes a different decision. Ugh.
														// They'll just shoot when trying to equip this with a weapon
		if (!player.getDisplayName().equals(this.ownerName)) { return false; }	// Not the owner, so not doing this
		
		ItemStack itemstack = player.inventory.getCurrentItem();
		
		// Isn't holding anything
		if (itemstack == null) 
		{
			if (player.isSneaking())	// They're sneaking, so removing our weapon now
			{
				if (!this.hasFirstWeapon)	// Not holding a primary weapon, meaning I'm not holding anything right now. So folding myself up
				{
					AI_Storage.dropSelf(this);
				}
				else	// Holding a weapon, so dropping it and all ammo
				{
					AI_Storage.dropFirstWeapon(this);
					AI_Storage.dropSecondWeapon(this);
					AI_Storage.dropStoredItems(this);
				}
				
				return true;
			}
			else if (this.hasRidingUpgrade)	// Not sneaking and we have the riding upgrade, so the owner can giddy up
	        {
				player.mountEntity(this);	
	            return true;
	        }
			
			return false; 
		}
		
		// Holding a weapon
		else if (itemstack.getItem() instanceof _WeaponBase) 
		{
			if (!player.capabilities.isCreativeMode) { player.setCurrentItemOrArmor(0, null); }	// Taking that
			
			if (this.hasWeaponUpgrade && player.isSneaking())
			{
				AI_WeaponHandler.setSecondWeapon(this, itemstack.copy());	// Equipping it in the second weapon slot, since we have that space
			}
			else	// Either not sneaking or doesn't have that weapon upgrade. Works for me
			{
				AI_WeaponHandler.setFirstWeapon(this, itemstack.copy());	// Equipping it
			}
			
			return true;
		}
		
		else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.iron_block)) // Holding repair material
		{
			AI_Properties.doRepair(player, this, itemstack);
		}
		
		else if (itemstack.getItem() == Items.name_tag) // Holding a name tag
		{
			AI_Properties.applyNameTag(player, this, itemstack, true);
		}
		
		else	// Holding whatever
		{
			AI_Storage.addItem(player, this, itemstack);
			return true;
		}
		
		return false;
    }
	
	
	@Override
	public void onDeath(DamageSource dmg)
    {
        if (ForgeHooks.onLivingDeath(this, dmg)) return;
        Entity entity = dmg.getEntity();
        EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (this.scoreValue >= 0 && entitylivingbase != null) { entitylivingbase.addToPlayerScore(this, this.scoreValue); }

        if (entity != null) { entity.onKillEntity(this); }	// Informing the killer about this
        
        this.worldObj.playSoundAtEntity(this, "random.break", 0.8f, 0.3f);

        if (!this.worldObj.isRemote)	// Spill it all (server-side)
        {
        	AI_Storage.dropFirstWeapon(this);
        	if (this.hasWeaponUpgrade) { AI_Storage.dropSecondWeapon(this); }
        	AI_Storage.dropParts(this);
        	AI_Storage.dropStoredItems(this);
        	
        	if (this.hasCommunicationUpgrade && AI_Targeting.isNameOnWhitelist(this, Commands.cmdTellDeath))
        	{
        		AI_Communication.tellOwnerAboutDeath(this);	// Whelp, you should probably know about this
        	}
        }
        
        this.dead = true;
        this.func_110142_aN().func_94549_h();

        this.worldObj.setEntityState(this, (byte) 3);
    }
	
	
	@Override
	protected String getHurtSound()
    {
        return "random.anvil_land";
    }
	
	
	@Override
	public boolean canBeSteered()
    {
        return this.hasRidingUpgrade;	// Can be steered if we have this upgrade
    }
	
	
	@Override
	protected void damageEntity(DamageSource dmgSource, float dmg)
    {
		if (this.isEntityInvulnerable()) { return; }	// Nothing to be done here
        
        dmg = ForgeHooks.onLivingHurt(this, dmgSource, dmg);
        if (dmg <= 0) return;
        
        dmg = this.applyArmorCalculations(dmgSource, dmg);
        dmg = this.applyPotionDamageCalculations(dmgSource, dmg);
        
        if (this.hasHeavyPlatingUpgrade)
        {
        	if (!dmgSource.isUnblockable()) { dmg -= this.armorPlatingDmgReduction; }		// If it can be blocked we can defend against it
        	else if (dmgSource.isFireDamage()) { dmg -= this.armorPlatingDmgReduction; }	// Fire is usually unblockable, hm?
        }
        
        if (dmgSource.getDamageType().equals("inWall")) { dmg = 0; }		// Not suffocating in a wall
    	else if (dmgSource.getDamageType().equals("starve")) { dmg = 0; }	// Don't need to eat
        
        if (dmg <= 0) return;
        
        // Damage absorption, if we have it
        float tempDmg = dmg;
        dmg = Math.max(dmg - this.getAbsorptionAmount(), 0.0F);
        this.setAbsorptionAmount(this.getAbsorptionAmount() - (tempDmg - dmg));

        if (dmg != 0.0F)
        {
            float health = this.getHealth();
            this.setHealth(health - dmg);
            this.func_110142_aN().func_94547_a(dmgSource, health, dmg);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - dmg);
        }
        // else, damage is 0. Nothing to be done here
        
        if (!this.worldObj.isRemote && this.getHealth() < this.getMaxHealth() / 3)
        {
        	// Has less than a third health left
        	if (this.hasCommunicationUpgrade && AI_Targeting.isNameOnWhitelist(this, Commands.cmdTellHealth))
        	{
        		AI_Communication.tellOwnerAboutHealth(this);
        	}
        }
    }
	
	
	@Override
	public void writeEntityToNBT(NBTTagCompound tag)
    {
		super.writeEntityToNBT(tag);
		
		if (this.worldObj.isRemote) { return; }	// Not doing the rest on client side
		
		tag.setString("owner", this.ownerName);
		tag.setDouble("movementSpeed", this.movementSpeed);
		
		tag.setBoolean("hasFirstWeapon", this.hasFirstWeapon);
		tag.setBoolean("hasSecondWeapon", this.hasSecondWeapon);
		
		if (this.hasFirstWeapon) 
		{ 
			tag.setInteger("firstWeapon", Main.weapons.indexOf(this.firstWeapon)); 
			tag.setInteger("firstAmmo", this.firstWeapon.getDamage(this.getHeldItem()));
		}
		
		if (this.hasSecondWeapon) 
		{ 
			tag.setInteger("secondWeapon", Main.weapons.indexOf(this.secondWeapon)); 
			tag.setInteger("secondAmmo", this.secondWeapon.getDamage(this.getEquipmentInSlot(1)));
		}
		
		tag.setBoolean("hasArmorUpgrade", this.hasArmorUpgrade);
        tag.setBoolean("hasMobilityUpgrade", this.hasMobilityUpgrade);
        tag.setBoolean("hasStorageUpgrade", this.hasStorageUpgrade);
        tag.setBoolean("hasWeaponUpgrade", this.hasWeaponUpgrade);
        tag.setBoolean("hasRidingUpgrade", this.hasRidingUpgrade);
        tag.setBoolean("hasHeavyPlatingUpgrade", this.hasHeavyPlatingUpgrade);
        tag.setBoolean("hasCommunicationUpgrade", this.hasCommunicationUpgrade);
        
        NBTTagList itemList = new NBTTagList();
        NBTTagCompound itemTag;

        int counter = 0;
        
        // Saving all stored items
        while (counter < this.storage.length)
        {
        	itemTag = new NBTTagCompound();

            if (this.storage[counter] != null) { this.storage[counter].writeToNBT(itemTag); }

            itemList.appendTag(itemTag);
            
        	counter += 1;
        }

        tag.setTag("storedItems", itemList);	// And attaching them to the tag list
        
        // Storing stationary coords, in case we have them
        tag.setDouble("stationaryX", this.stationaryX);
        tag.setDouble("stationaryY", this.stationaryY);
        tag.setDouble("stationaryZ", this.stationaryZ);
    }
	
	
	@Override
	public void readEntityFromNBT(NBTTagCompound tag)
    {
		super.readEntityFromNBT(tag);
		
		if (this.worldObj.isRemote) { return; }	// Not doing the rest on client side
		
		this.ownerName = tag.getString("owner");
		this.movementSpeed = tag.getDouble("movementSpeed");
		
		// Restoring Upgrades
		this.hasArmorUpgrade = tag.getBoolean("hasArmorUpgrade");
		this.hasMobilityUpgrade = tag.getBoolean("hasMobilityUpgrade");
		this.hasStorageUpgrade = tag.getBoolean("hasStorageUpgrade");
		this.hasWeaponUpgrade = tag.getBoolean("hasWeaponUpgrade");
		this.hasRidingUpgrade = tag.getBoolean("hasRidingUpgrade");
		this.hasHeavyPlatingUpgrade = tag.getBoolean("hasHeavyPlatingUpgrade");
		this.hasCommunicationUpgrade = tag.getBoolean("hasCommunicationUpgrade");
		
		this.hasFirstWeapon = tag.getBoolean("hasFirstWeapon");	// Were you holding a weapon before?

		// Restoring the held weapon
		if (this.hasFirstWeapon)
		{
			this.firstWeapon = Main.weapons.get(tag.getInteger("firstWeapon"));
			AI_WeaponHandler.setFirstWeapon(this, new ItemStack(Main.weapons.get(tag.getInteger("firstWeapon"))));
			
			if (this.getHeldItem() != null)
			{
				this.getHeldItem().setItemDamage(tag.getInteger("firstAmmo"));	// restoring known ammo
			}
		}

		if (this.hasStorageUpgrade) { AI_Properties.applyStorageUpgrade(this); }	// Restoring the fact that we've got this (also tells the client about this)
		
		this.hasSecondWeapon = tag.getBoolean("hasSecondWeapon");	// Were you holding a weapon before?
		
		if (this.hasWeaponUpgrade && this.hasSecondWeapon)
		{
			AI_WeaponHandler.setSecondWeapon(this, new ItemStack(Main.weapons.get(tag.getInteger("secondWeapon"))));
			
			if (this.getHeldItem() != null)
			{
				this.getHeldItem().setItemDamage(tag.getInteger("firstAmmo"));	// restoring known ammo
			}
		}
		
		// Restoring items
		if (tag.hasKey("storedItems", 9))
        {
			NBTTagList itemList = tag.getTagList("storedItems", 10);

			int counter = 0;
            
			while (counter < this.storage.length)
	        {
                this.storage[counter] = ItemStack.loadItemStackFromNBT(itemList.getCompoundTagAt(counter));
                
                counter += 1;
            }
        }
		
		// Restoring stationary coords, in case we have them
		this.stationaryX = tag.getDouble("stationaryX");
		this.stationaryY = tag.getDouble("stationaryY");
		this.stationaryZ = tag.getDouble("stationaryZ");
    }
}
