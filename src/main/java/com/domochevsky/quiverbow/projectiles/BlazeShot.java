package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class BlazeShot extends _ProjectileBase
{
	public BlazeShot(World world) { super(world); }
	
	public BlazeShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }

	
	@Override
	public void onImpact(MovingObjectPosition hitPos)	// Server-side
	{
		if (hitPos.entityHit != null)
        {
			// Setting fire to the target here ...except for endermen? First fire, then damage
			if (!(hitPos.entityHit instanceof EntityEnderman)) { hitPos.entityHit.setFire(this.fireDuration); }
						
			// Damage
			hitPos.entityHit.attackEntityFrom( DamageSource.causeThrownDamage(this, this.shootingEntity), this.damage);	
			hitPos.entityHit.hurtResistantTime = 0;	// No rest for the wicked

			// Knockback
            double f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
           
            if (f3 > 0.0F) 
            {
            	hitPos.entityHit.addVelocity(
        			this.motionX * (double)this.knockbackStrength * 0.6D / (double)f3, 
        			0.1D, 
        			this.motionZ * (double)this.knockbackStrength * 0.6D / (double)f3
            	);
            }
			
            if (!(hitPos.entityHit instanceof EntityEnderman)) { this.setDead(); }	// We've hit an entity (that's not an enderman), so begone with the projectile

	    	this.playSound("random.fizz", 0.5F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));			// Sizzling along...
        }
        else
        {        	
        	Block block = this.worldObj.getBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ);
        	
        	// Let's melt ice on contact
            if (block == Blocks.ice) 
            { 
            	this.worldObj.setBlock(hitPos.blockX, hitPos.blockY, hitPos.blockZ, Blocks.flowing_water, 0, 3); 
            	this.targetsHit += 1;
            }
            
            // Glass breaking, through 4 layers
            if (Helper.tryBlockBreak(this.worldObj, this, hitPos, 2) && this.targetsHit < 4) { this.targetsHit += 1; } // Going straight through most things
            else	// Either didn't manage to break that block or we already hit 4 things
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
		//System.out.println("Caller is " + this + "/ worldObj is " + this.worldObj + " / entity ID is " + this.getEntityId());
		
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 3);
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 4, (byte) 1);
	}
	
	
	@Override
	public void doInGroundSFX() // Server side
	{
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 1);
		//NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 11, (byte) 4);
		this.playSound("random.fizz", 0.1F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));			// Sizzling along...
		
		this.targetsHit += 1;	// Dissipating in strength each tick?
	}
	
	
	@Override
	public void doWaterEffect() // Called when this entity moves through water
	{ 
		// Checking for water here and turning it into ice
		int x = (int) this.posX;
		int y = (int) this.posY;
		int z = (int) this.posZ;
		
		Block hitBlock = this.worldObj.getBlock(x, y, z);
		
		if (hitBlock == Blocks.water || hitBlock == Blocks.flowing_water)
		{
			// Hit a (flowing) water block, so turning that into ice now
			this.worldObj.setBlockToAir(x, y, z);
			
			// SFX
			NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 4);
			this.playSound("random.fizz", 0.1F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
		}
	}
	
	
	@Override
	public void onCollideWithPlayer(EntityPlayer player)	// Burning while stuck in the ground
    {
		if (this.worldObj.isRemote) { return; }	// Not doing this on client side
		if (!this.inGround) { return; }			// Not stuck in the ground
		if (this.arrowShake > 0) { return; }	// Not... done shaking?
		
		// Ready to hurt someone!
		player.setFire(this.fireDuration / 2);	// Half burn time. Let's be lenient here
    }
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Generic projectile
		type[1] = 6;	// Length and width
		type[2] = 2;
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/rod.png"; }
}
