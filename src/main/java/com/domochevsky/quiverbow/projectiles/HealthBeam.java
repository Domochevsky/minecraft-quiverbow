package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.ShotPotion;
import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class HealthBeam extends _ProjectileBase implements IEntityAdditionalSpawnData
{
	public int travelTicksMax;
	
	public HealthBeam(World world) { super(world); }

	public HealthBeam(World world, Entity entity, float speed) 
    {
        super(world);
        this.doSetup(entity, speed, 0, 0, entity.rotationYaw, entity.rotationPitch);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + entity.getEyeHeight() - 0.2;
        this.ownerZ = entity.posZ;
    }
	
	
	public HealthBeam(World world, Entity entity, Entity target) 
    {
        super(world);
        this.doSetup(entity, target, entity.rotationYaw, entity.rotationPitch);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + entity.getEyeHeight() - 0.2;
        this.ownerZ = entity.posZ;
    }
	
	
	public void doSetup(Entity entity, Entity target, float setYaw, float setPitch)	// Default setup for all shot projectiles
	{
		this.renderDistanceWeight = 10.0D;
		if (entity instanceof EntityLivingBase) { this.shootingEntity = (EntityLivingBase) entity; }

        Helper.setThrownPickup(this.shootingEntity, this);	// Taking care of pickupability

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ, setYaw, setPitch);
        
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        
        this.setPosition(this.posX, this.posY, this.posZ);
        
        this.yOffset = 0.0F;
        
        this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        this.motionY = (double) (-MathHelper.sin((this.rotationPitch) / 180.0F * (float) Math.PI));
        
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 0, 1.0F);
	}
	
	
	@Override
	public boolean doDropOff() { return false; }	// Affected by gravity?
	
	
	@Override
	public void doFlightSFX() 
	{
		if (this.ticksExisted > this.ticksInAirMax) { this.setDead(); }	// There's only so long we can exist
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 10, (byte) 10);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition movPos) 
	{
		if (movPos.entityHit != null) 		// We hit a living thing!
    	{		
			if (movPos.entityHit instanceof EntityLivingBase)	// We hit a LIVING living thing!
            {
	            EntityLivingBase living = (EntityLivingBase) movPos.entityHit;
	            
	            if (living.isEntityUndead())	// Not affected by regen potions, so whithering them instead
	            {
	            	ShotPotion pot1 = new ShotPotion();
		            
		            pot1.potion = Potion.wither;
		            pot1.Strength = 3;
		            pot1.Duration = 20;
		            
		            Helper.applyPotionEffect(living, pot1);
	            }
	            else	// Applying regen
	            {
	            	// Healing
		            float health = living.getHealth();
		            float healthMax = living.getMaxHealth();
		            
		            if (health >= healthMax && living.getAbsorptionAmount() < healthMax)	// Can be buffed up to 200% health. Might need to be adjusted
		            {
		            	living.setAbsorptionAmount(living.getAbsorptionAmount() + 1);
		            } 
		            // else, either not at full health or already full up on absorption
		            
		            ShotPotion pot1 = new ShotPotion();
		            
		            pot1.potion = Potion.regeneration;
		            pot1.Strength = 3;
		            pot1.Duration = 20;
		            
		            Helper.applyPotionEffect(living, pot1);
	            }
            }
        }        
		// else, hit the terrain
    	
		// SFX
    	this.worldObj.playSoundAtEntity(this, "random.fizz", 0.7F, 1.5F);
    	NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 10, (byte) 7);
    	
    	this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 8;	// Type 7, beam weapon (sunray)
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/healthbeam.png"; }	// Our projectile texture
	
	
	@Override
	public void writeSpawnData(ByteBuf buffer) 			// save extra data on the server
    {
		buffer.writeDouble(this.ownerX);
		buffer.writeDouble(this.ownerY);
		buffer.writeDouble(this.ownerZ);
    }
    
	@Override
	public void readSpawnData(ByteBuf additionalData) 	// read it on the client
	{ 
		this.ownerX = additionalData.readDouble();
		this.ownerY = additionalData.readDouble();
		this.ownerZ = additionalData.readDouble();
	}
}
