package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class RegularArrow extends _ProjectileBase implements IProjectile
{  	
	public RegularArrow(World world) { super(world); }
	
	
	public RegularArrow(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }	
	
	
	public RegularArrow(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	@Override
	public void onImpact(MovingObjectPosition hitPos)	// Server-side
	{
		//Console.out().println("Client side: " + this.worldObj.isRemote);
		
		if (hitPos.entityHit != null)	// Hit an entity
        {
            if (hitPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), this.damage))	// Attacking
            {
            	//System.out.println("Damage dealt: " + this.damage);
            	hitPos.entityHit.hurtResistantTime = 0;	// No rest for the wicked
            	
                if (hitPos.entityHit instanceof EntityLivingBase)
                {
                    EntityLivingBase entity = (EntityLivingBase) hitPos.entityHit;

                  	//if (!this.worldObj.isRemote) { entity.setArrowCountInEntity(entity.getArrowCountInEntity() + 1); } // Arrow count, eh? Server-only
                    entity.setArrowCountInEntity(entity.getArrowCountInEntity() + 1);

                    // Knockback
                    if (this.knockbackStrength > 0)
                    {
                    	float distance = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (distance > 0.0F)
                        {
                            hitPos.entityHit.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6D / (double) distance, 
                            		0.1D, 
                            		this.motionZ * (double) this.knockbackStrength * 0.6D / (double) distance);
                        }
                    }
                    
                    // Whazzat?
                    if (this.shootingEntity != null && 
                    		hitPos.entityHit != this.shootingEntity && 
                    		hitPos.entityHit instanceof EntityPlayer && 
                    		this.shootingEntity instanceof EntityPlayerMP)
                    {
                    	EntityPlayerMP playerMP = (EntityPlayerMP) this.shootingEntity;
                    	playerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));	// What're ya sending here?
                    }
                    // shooter isn't null, target isn't shooter, shooter is a player and a multiplayer
                }

                // SFX
                this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                if (!(hitPos.entityHit instanceof EntityEnderman)) { this.setDead(); }	// We're done here, assuming we didn't (non)hit an enderman
            }
            else	// Didn't succeed in attacking the target? Bouncing off
            {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                
                this.ticksInAir = 0;
            }
        }
        else	// Hit the terrain. Looks like we need to keep doing this on client side as well, to properly update arrows stuck in the ground
        {
        	if (Helper.tryBlockBreak(this.worldObj, this, hitPos, 1) && this.targetsHit < 1) { this.targetsHit += 1; } // Going straight through glass
            else	// Either didn't manage to break that block or we already hit a thing
            {
		        this.stuckBlockX = hitPos.blockX;
		        this.stuckBlockY = hitPos.blockY;
		        this.stuckBlockZ = hitPos.blockZ;
		        
		        this.stuckBlock = this.worldObj.getBlock(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);
		        this.inData = this.worldObj.getBlockMetadata(this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ);
		        
		        this.motionX = (double)((float)(hitPos.hitVec.xCoord - this.posX));
		        this.motionY = (double)((float)(hitPos.hitVec.yCoord - this.posY));
		        this.motionZ = (double)((float)(hitPos.hitVec.zCoord - this.posZ));
		        
		        float distance = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		        
		        this.posX -= this.motionX / (double)distance * 0.05000000074505806D;
		        this.posY -= this.motionY / (double)distance * 0.05000000074505806D;
		        this.posZ -= this.motionZ / (double)distance * 0.05000000074505806D;
		        
		        // SFX
		        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
		        
		        this.inGround = true;
		        
		        this.arrowShake = 7;
		
		        if (this.stuckBlock.getMaterial() != Material.air)
		        {
		            this.stuckBlock.onEntityCollidedWithBlock(this.worldObj, this.stuckBlockX, this.stuckBlockY, this.stuckBlockZ, this);
		        }
            }
        }
	}
	
	
	@Override
	public void doFlightSFX() 
	{ 
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 13, (byte) 2);
	}
	
	
	@Override
	public void onCollideWithPlayer(EntityPlayer player)	// Arrow pickup
    {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.canBePickedUp;

            // Can we add this arrow to the player's inventory?
            if (this.canBePickedUp && !player.inventory.addItemStackToInventory(new ItemStack(Items.arrow, 1))) { flag = false; }
            // else, either can't pick this arrow up in general, making this a moot point, or the player's inventory is somehow full

            if (flag)
            {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 1;	// Regular arrow
		//type[1] = 0;	// Length and width doesn't matter
		//type[2] = 0;
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return null; }	// The renderer uses the internal arrow texture, so we don't need to provide anything
}
