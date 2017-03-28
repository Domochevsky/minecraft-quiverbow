package com.domochevsky.quiverbow.projectiles;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import com.domochevsky.quiverbow.net.NetHelper;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class FlintDust extends _ProjectileBase implements IEntityAdditionalSpawnData
{	
	public FlintDust(World world) { super(world); }

	public FlintDust(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
        
        this.ownerX = entity.posX;
        this.ownerY = entity.posY + 1.0d;
        this.ownerZ = entity.posZ;
    }
	
	
	@Override
	public boolean doDropOff() { return false; }	// Affected by gravity? Nope

	
	@Override
	public void doFlightSFX()
	{
		if (this.shootingEntity == null) { return; }	// Shouldn't be a thing
		
		Vec3 vec_entity = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 vec_shooter = Vec3.createVectorHelper(this.shootingEntity.posX, this.shootingEntity.posY, this.shootingEntity.posZ);
        
        double distance = vec_entity.distanceTo(vec_shooter);	// The distance between this entity and the shooter
        
        //System.out.println("[ENTITY] Distance to shooter: " + distance);
        if (distance > this.ticksInAirMax - 2) { this.setDead(); }	// Starting 0.5 blocks in front of the player and ends one+ block after the target. So ending now
        NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 1);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) 		// We hit a living thing!
    	{		
			// Damage
			target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float)this.damage);
        }
		else	// Hit the terrain
    	{
    		Block block = this.worldObj.getBlock(target.blockX, target.blockY, target.blockZ);            
            int meta = this.worldObj.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
            
            boolean breakThis = true;
            
            // Checking here against invalid blocks
        	if (block == Blocks.bedrock) { breakThis = false; }
        	else if (block == Blocks.water) { breakThis = false; }
        	else if (block == Blocks.flowing_water) { breakThis = false; }
        	else if (block == Blocks.lava) { breakThis = false; }
        	else if (block == Blocks.flowing_lava) { breakThis = false; }
        	else if (block == Blocks.obsidian) { breakThis = false; }
        	else if (block == Blocks.mob_spawner) { breakThis = false; }
        	
        	else if (block.getMaterial() == Material.water) { breakThis = false; }
        	else if (block.getMaterial() == Material.lava) { breakThis = false; }
        	else if (block.getMaterial() == Material.air) { breakThis = false; }
        	else if (block.getMaterial() == Material.portal) { breakThis = false; }
        	
        	else if (block.getHarvestLevel(meta) > 0) { breakThis = false; }
        	else if (block.getBlockHardness(this.worldObj, target.blockX, target.blockY, target.blockZ) > 3) { breakThis = false; }
        	
        	if (this.shootingEntity instanceof EntityPlayerMP)
        	{
        		WorldSettings.GameType gametype = this.worldObj.getWorldInfo().getGameType();
            	BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(this.worldObj, gametype, (EntityPlayerMP) this.shootingEntity, target.blockX, target.blockY, target.blockZ);
               
            	if (event.isCanceled()) { breakThis = false; }	// Not allowed to do this
        	}
            
            if (breakThis)	// Nothing preventing us from breaking this block!
            {            	
            	this.worldObj.setBlockToAir(target.blockX, target.blockY, target.blockZ);
            	block.dropBlockAsItem(this.worldObj, target.blockX, target.blockY, target.blockZ, meta, 0);
            }
    	}
		
		// SFX
		for (int i = 0; i < 4; ++i) { this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D); }
		this.worldObj.playSoundAtEntity(this, Block.soundTypeGravel.getBreakSound(), 1.0F, 1.0F);
		
		this.setDead();	// Hit something, so begone.
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 5;	// Type 5, beam weapon (Flint dust)
		type[1] = 2;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/flint.png"; }	// Our projectile texture
	
	
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
