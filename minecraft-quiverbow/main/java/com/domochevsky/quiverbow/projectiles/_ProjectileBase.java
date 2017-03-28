package com.domochevsky.quiverbow.projectiles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class _ProjectileBase extends Entity implements IProjectile
{
	// Used by projectiles that care about the original position of the shooter, like beam weapons
	public double ownerX;
	public double ownerY;
	public double ownerZ;
	
	public int stuckBlockX = -1;
	public int stuckBlockY = -1;
	public int stuckBlockZ = -1;
    
	public Block stuckBlock;
	public int inData;	// metadata of the block we're stuck in
	
	public EntityLivingBase shootingEntity;
	
	public int ticksInGround;
	public int ticksInGroundMax;
	public int ticksInAir;
	public int ticksInAirMax;
	
	public boolean inGround;	// Turns true if we hit a block and are stuck in them
	
	public int damage;
	public int knockbackStrength;
	public int fireDuration;
	public double explosionSize;
	
	public boolean canBePickedUp;
	
	public int arrowShake;
	
	public int targetsHit;	// The number of targets we've hit so far. Relevant for breaking through glass
	
	private int netCooldown;	// This decreases until it has reached 0, after which it'll send a position update packet and then resets itself to max
	public int netCooldownMax = 10;	// Time in ticks until we send another position update packet
	

	public _ProjectileBase(World world) { super(world); }
	
	
	public void doSetup(Entity entity, float speed)	// Default setup for all shot projectiles
	{
		this.doSetup(entity, speed, 0f, 0f, entity.rotationYaw, entity.rotationPitch);
	}
	
	
	public void doSetup(Entity entity, float speed, float accHor, float accVert, float setYaw, float setPitch)	// Default setup for all shot projectiles
	{
		this.renderDistanceWeight = 10.0D;
		if (entity instanceof EntityLivingBase) { this.shootingEntity = (EntityLivingBase) entity; }

        Helper.setThrownPickup(this.shootingEntity, this);	// Taking care of pickupability

        this.setSize(0.5F, 0.5F);
        this.setLocationAndAngles(entity.posX, entity.posY + (double) entity.getEyeHeight(), entity.posZ, setYaw, setPitch);
        
        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        this.posY -= 0.10000000149011612D;
        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
        
        this.setPosition(this.posX, this.posY, this.posZ);
        
        this.yOffset = 0.0F;
        
        //this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        //this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
        //this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
        
        this.motionX = (double)(-MathHelper.sin((this.rotationYaw + accHor) / 180.0F * (float) Math.PI) * MathHelper.cos((this.rotationPitch + accVert) / 180.0F * (float)Math.PI));
        this.motionZ = (double)(MathHelper.cos((this.rotationYaw + accHor) / 180.0F * (float) Math.PI) * MathHelper.cos((this.rotationPitch + accVert) / 180.0F * (float)Math.PI));
        this.motionY = (double)(-MathHelper.sin(((this.rotationPitch + accVert)) / 180.0F * (float) Math.PI));
        
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, 1.0F);
	}
	
	
	@SideOnly(Side.CLIENT)
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_)
    {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70016_3_, (double)f) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

	
	@Override
	public void setThrowableHeading(double motX, double motY, double motZ, float speed, float unknown) 
	{
		float f2 = MathHelper.sqrt_double(motX * motX + motY * motY + motZ * motZ);
        motX /= (double)f2;
        motY /= (double)f2;
        motZ /= (double)f2;
        
        motX += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) unknown;
        motY += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) unknown;
        motZ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double) unknown;
        
        motX *= (double) speed;
        motY *= (double) speed;
        motZ *= (double) speed;
        
        this.motionX = motX;
        this.motionY = motY;
        this.motionZ = motZ;
        
        float f3 = MathHelper.sqrt_double(motX * motX + motZ * motZ);
        
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(motX, motZ) * 180.0D / Math.PI);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(motY, (double)f3) * 180.0D / Math.PI);
        
        this.ticksInGround = 0;
	}
	

	@Override
	@SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int unknown)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }
	
	
	@Override
	protected void entityInit() { this.dataWatcher.addObject(16, Byte.valueOf((byte)0)); }

	
	@Override
	public void onUpdate()
    {
        super.onUpdate();
        
        if (this.worldObj.isRemote) { return; }	// Not doing this on client side

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }

        Block potentialBlock = this.worldObj.getBlock(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);

        if (potentialBlock.getMaterial() != Material.air)
        {
            potentialBlock.setBlockBoundsBasedOnState(this.worldObj, this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);
            AxisAlignedBB potentialAABB = potentialBlock.getCollisionBoundingBoxFromPool(this.worldObj, this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);

            if (potentialAABB != null && potentialAABB.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;	// Hit a non-air block, so we're now stuck in the ground
            }
        }

        if (this.arrowShake > 0) { --this.arrowShake; }	// Likely animation-relevant

        if (this.inGround)
        {
        	doInGroundSFX();	// Stuck in the ground, so ground SFX is go
        	
        	 if (this.netCooldown <= 0)	// Time to send an update again
         	{
         		NetHelper.sendPositionMessageToPlayersInRange(this.worldObj, this, this.posX, this.posY, this.posZ);	// Explicitly making our position known, for precision
         		this.netCooldown = this.netCooldownMax;	// Every X ticks, so resetting that now
         	}
         	else	// It's not time yet
         	{
         		this.netCooldown -= 1;	// One tick less
         	}
        	
            int meta = this.worldObj.getBlockMetadata(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);

            if (potentialBlock == this.stuckBlock && meta == this.inData)
            {
                ++this.ticksInGround;

                if (this.ticksInGroundMax != 0 && this.ticksInGround > this.ticksInGroundMax) { this.setDead(); }	// Ticks max is not 0, so we care about it
                else if (this.ticksInGround == 1200) { this.setDead(); }	// Generally overaged. You're done
            }
            else	// Not in the same block anymore, so starting to move again
            {
                this.inGround = false;
                
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
               
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        }
        else
        {
            ++this.ticksInAir;	// Aging along
            
            Vec3 currentVec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 futureVec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            
            MovingObjectPosition hitPos = this.worldObj.func_147447_a(currentVec3, futureVec3, false, true, false);
            
            // This seems to require a reset, since getRayTrace messes with them?
            currentVec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            futureVec3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (hitPos != null)	// Hit something
            {
                futureVec3 = Vec3.createVectorHelper(hitPos.hitVec.xCoord, hitPos.hitVec.yCoord, hitPos.hitVec.zCoord);
            }

            Entity hitEntity = null;
            List candidateList = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
           
            double d0 = 0.0D;
            int iteratori;
            float gravity = 0.3F;

            for (iteratori = 0; iteratori < candidateList.size(); ++iteratori)
            {
                Entity potentialEntity = (Entity)candidateList.get(iteratori);

                if (potentialEntity.canBeCollidedWith() && (potentialEntity != this.shootingEntity || this.ticksInAir >= 5) && !(potentialEntity instanceof EntityPlayer))
                {
                    AxisAlignedBB axisalignedbb1 = potentialEntity.boundingBox.expand((double) gravity, (double) gravity, (double) gravity);
                    MovingObjectPosition potentialMovObj = axisalignedbb1.calculateIntercept(currentVec3, futureVec3);

                    if (potentialMovObj != null)
                    {
                        double d1 = currentVec3.distanceTo(potentialMovObj.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            hitEntity = potentialEntity;
                            d0 = d1;
                        }
                    }
                }
            }

            if (hitEntity != null) {  hitPos = new MovingObjectPosition(hitEntity); }	// Hit an entity, so grabbing its position

            if (hitPos != null && hitPos.entityHit != null && hitPos.entityHit instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)hitPos.entityHit;

                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
                {
                    hitPos = null;	// Either his entity can't be damaged in general or we can't attack them
                }
            }
            
            if (hitPos != null) // Hit something
            { 
            	// Terrain protection could be applied here
            	this.onImpact(hitPos); 
            }	
            this.doFlightSFX();

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            
            float distance = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            
            this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            // The fuck is this?
            for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)distance) * 180.0D / Math.PI); 
            		this.rotationPitch - this.prevRotationPitch < -180.0F; 
            		this.prevRotationPitch -= 360.0F)
            { ; }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) { this.prevRotationPitch += 360.0F; }
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) { this.prevRotationYaw -= 360.0F; }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) { this.prevRotationYaw += 360.0F; }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            
            float speedDecrease = 0.99F;
            gravity = 0.05F;
            float sfxMod = 0.25F;

            if (this.isInWater())
            {
                for (int l = 0; l < 4; ++l)
                {
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) sfxMod, 
                    		this.posY - this.motionY * (double) sfxMod, 
                    		this.posZ - this.motionZ * (double) sfxMod, 
                    		this.motionX, this.motionY, this.motionZ);
                }

                speedDecrease = 0.8F;
                // Might be a good spot to apply special things as well, for projectiles that don't like water
                
                this.doWaterEffect();
            }
            
            //this.moveEntity(this.motionX, this.motionY, this.motionZ); 	// Alternate method. Maybe this will give us smoother movement?
            // Doesn't seem to make much of a difference

            if (this.isWet()) { this.extinguish(); }

            this.motionX *= (double) speedDecrease;
            this.motionY *= (double) speedDecrease;
            this.motionZ *= (double) speedDecrease;
            
            if (this.doDropOff()) { this.motionY -= (double) gravity; }
            
            this.setPosition(this.posX, this.posY, this.posZ);	// Position update
            //NetHelper.sendPositionMessageToAllPlayers(this.worldObj, this.getEntityId(), this.posX, this.posY, this.posZ);	// Informing the client about our position change explicitly
            
            this.func_145775_I();	// block collision
        }
    }
	
	
	public void doWaterEffect() { }	// Called when this entity moves through water


	public void onImpact(MovingObjectPosition hitPos) { }	// Server side
	
	
	public void doFlightSFX() { }	// Server side
	public void doInGroundSFX() { }	// Server side
	
	public boolean doDropOff() { return true; }	// If this returns false then we won't care about gravity


	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) 
	{
		this.stuckBlockX = tag.getShort("xTile");
        this.stuckBlockY = tag.getShort("yTile");
        this.stuckBlockZ = tag.getShort("zTile");
        
        this.ticksInGround = tag.getShort("life");
        this.ticksInGroundMax = tag.getShort("lifeMax");
        this.ticksInAirMax = tag.getShort("lifeInAirMax");
        
        this.stuckBlock = Block.getBlockById(tag.getByte("inTile") & 255);
        
        this.inData = tag.getByte("inData") & 255;
        this.arrowShake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;

        if (tag.hasKey("damage", 99)) { this.damage = tag.getInteger("damage"); }
        if (tag.hasKey("pickup", 99)) { this.canBePickedUp = tag.getBoolean("pickup"); }
        else if (tag.hasKey("player", 99)) { this.canBePickedUp = tag.getBoolean("player") ? true : false; }
	}


	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) 
	{
		tag.setShort("xTile", (short)this.stuckBlockX);
        tag.setShort("yTile", (short)this.stuckBlockY);
        tag.setShort("zTile", (short)this.stuckBlockY);
        
        tag.setShort("life", (short) this.ticksInGround);
        tag.setShort("lifeMax", (short) this.ticksInGroundMax);
        tag.setShort("lifeInAirMax", (short) this.ticksInAirMax);
        
        tag.setByte("inTile", (byte)Block.getIdFromBlock(this.stuckBlock));
        
        tag.setByte("inData", (byte)this.inData);
        
        tag.setByte("shake", (byte)this.arrowShake);
        
        tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        
        tag.setBoolean("pickup", (boolean)this.canBePickedUp);
        
        tag.setDouble("damage", this.damage);
	}

	
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		return type; // Fallback, 0 0 0
	}
	
	
	public String getEntityTexturePath() { return null; }	// Returns the texture path for this entity. Gets created by the renderer


	public Entity getShooter() 
	{
		return this.shootingEntity;
	}
}
