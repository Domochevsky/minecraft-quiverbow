package com.domochevsky.quiverbow.AI;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;

public class AI_Movement 
{
	public static void handleMovement(Entity_AA turret)
	{
		if (turret.riddenThisTick)	// Is currently being ridden by someone, so letting the owner take over
		{
			doRiding(turret);
			return; 
		}	// Easy there, we're being ridden
		
		if (turret.movementDelay > 0)	// Still ticking down
		{
			turret.movementDelay -= 1;	// Once a second
			return;
		}
		
		//System.out.println("[ARMS ASSISTANT] Checking movement.");
		
		turret.movementDelay = 20;	// Reset
		
		if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdFollowOwner))	// We're instructed to follow the owner, so running after them
		{
			EntityPlayer player = turret.worldObj.getPlayerEntityByName(turret.ownerName);
			
			//System.out.println("[ARMS ASSISTANT] Set to follow owner. Owner is " + player);
			
			if (player != null)
			{
				FollowOwner(turret, player);
			}
			else	// else, no player found. Now what? Wander randomly?
			{
				wanderRandomly(turret);
			}
			
		}
		else if (AI_Targeting.isNameOnWhitelist(turret, Commands.cmdStayStationary))	
		{
			// We're instructed to stay at our current position, so checking now if our position is different from where we need to be
			//System.out.println("[ARMS ASSISTANT] Set to stay in position.");
			
			if (turret.posX <= turret.stationaryX - 0.5 || turret.posX >= turret.stationaryX + 0.5 ||
					turret.posY <= turret.stationaryY - 0.5 || turret.posY >= turret.stationaryY + 0.5 ||
					turret.posZ <= turret.stationaryZ - 0.5 || turret.posZ >= turret.stationaryZ + 0.5)
			{
				turret.getNavigator().tryMoveToXYZ(turret.stationaryX, turret.stationaryY, turret.stationaryZ, (turret.movementSpeed));
			}
			// Else, still in position
		}
		else	// Just wandering randomly
		{
			//System.out.println("[ARMS ASSISTANT] No specifics set. Wandering randomly.");
			if (turret.canFly)	// Fly around randomly
			{
				flyRandomly(turret);
				//wanderRandomly(turret);
			}
			else	// Wander around randomly on the ground
			{
				wanderRandomly(turret);
			}
		}
	}
	
	
	private static void FollowOwner(Entity_AA turret, EntityPlayer owner)
	{
		if (turret.getDistanceSqToEntity(owner) < (double) (10.0F * 10.0F)) { return; }	// Still close enough (min distance is 10² blocks)
		
		if (!turret.getNavigator().noPath() && turret.getDistanceSqToEntity(owner) > (double) (2.0 * 2.0)) { return; }		// Already moving and distance to owner is beyond max 
		// !this.getNavigator().noPath() &&

        if (!turret.getNavigator().tryMoveToEntityLiving(owner, turret.movementSpeed))
        {
            if (turret.getDistanceSqToEntity(owner) >= 144.0D)
            {
                int blockPosX = MathHelper.floor_double(owner.posX) - 2;
                int blockPosY = MathHelper.floor_double(owner.posZ) - 2;
                int blockPosZ = MathHelper.floor_double(owner.boundingBox.minY);

                for (int counterX = 0; counterX <= 4; ++counterX)
                {
                    for (int counterY = 0; counterY <= 4; ++counterY)
                    {
                        if ((counterX < 1 || counterY < 1 || counterX > 3 || counterY > 3) && 
                        		World.doesBlockHaveSolidTopSurface(turret.worldObj, blockPosX + counterX, blockPosZ - 1, blockPosY + counterY) && 
                        		!turret.worldObj.getBlock(blockPosX + counterX, blockPosZ, blockPosY + counterY).isNormalCube() && 
                        		!turret.worldObj.getBlock(blockPosX + counterX, blockPosZ + 1, blockPosY + counterY).isNormalCube())
                        {
                        	turret.setLocationAndAngles((double)((float) (blockPosX + counterX) + 0.5F), 
                            		(double)blockPosZ, (double)((float)(blockPosY + counterY) + 0.5F),
                            		turret.rotationYaw, turret.rotationPitch);
                        	turret.getNavigator().clearPathEntity();
                            return;
                        }
                    }
                }
            }
            // else, owner is too close
        }
        // else, couldn't move to the owner for some reason
	}
	
	
	private static void wanderRandomly(Entity_AA turret)
	{
		if (!turret.getNavigator().noPath()) { return; } // Already going somewhere
		if (turret.getRNG().nextInt(20) != 0) { return; }	// Not yet
		
		//System.out.println("[ARMS ASSISTANT] Trying to wander randomly now.");
		
		Vec3 targetPos = findRandomTarget(turret, 10, 7);
		
		if (targetPos == null) { return; }	// Didn't get a valid position, I guess
		
		//System.out.println("[ARMS ASSISTANT] Got a target position. Going there.");
		
		turret.getNavigator().tryMoveToXYZ(targetPos.xCoord, targetPos.yCoord, targetPos.zCoord, turret.movementSpeed);	// Get moving
	}
	
	
	private static Vec3 findRandomTarget(Entity entity, int distanceXZ, int distanceY)
	{
		Random random = entity.worldObj.rand;
		
		int targetX = 0;
		int targetY = 0;
		int targetZ = 0;

        for (int l1 = 0; l1 < 10; ++l1)
        {
            int rangeX = random.nextInt(2 * distanceXZ) - distanceXZ;
            int rangeY = random.nextInt(2 * distanceY) - distanceY;
            int rangeZ = random.nextInt(2 * distanceXZ) - distanceXZ;

            rangeX += MathHelper.floor_double(entity.posX);
            rangeY += MathHelper.floor_double(entity.posY);
            rangeZ += MathHelper.floor_double(entity.posZ);

            targetX = rangeX;
            targetY = rangeY;
            targetZ = rangeZ;
        }

        return Vec3.createVectorHelper((double)targetX, (double)targetY, (double)targetZ);
	}
	
	
	private static void flyRandomly(Entity_AA aa)
	{
		double distanceX = aa.waypointX - aa.posX;
        double distanceY = aa.waypointY - aa.posY;
        double distanceZ = aa.waypointZ - aa.posZ;
        
        double distanceSq = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;

        if (distanceSq < 1.0D || distanceSq > 3600.0D)
        {
            aa.waypointX = aa.posX + (double)((aa.worldObj.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
            aa.waypointY = aa.posY + (double)((aa.worldObj.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
            aa.waypointZ = aa.posZ + (double)((aa.worldObj.rand.nextFloat() * 2.0F - 1.0F) * 16.0F);
        }

        if (aa.courseChangeCooldown-- <= 0)	// Idly flying around
        {        	
            aa.courseChangeCooldown += aa.worldObj.rand.nextInt(5) + 2;
            distanceSq = (double)MathHelper.sqrt_double(distanceSq);

            if (isCourseTraversable(aa, aa.waypointX, aa.waypointY, aa.waypointZ, distanceSq))
            {            	
            	//aa.moveFlying((float) aa.waypointX, (float) aa.waypointY, (float) aa.waypointZ);
            	
                aa.motionX += distanceX / distanceSq * 0.5D;
                aa.motionY += distanceY / distanceSq * 0.5D;
                aa.motionZ += distanceZ / distanceSq * 0.5D;
            }
            else
            {
                aa.waypointX = aa.posX;
                aa.waypointY = aa.posY;
                aa.waypointZ = aa.posZ;
            }
        }
	}
	
	
	private static boolean isCourseTraversable(Entity_AA aa, double posX, double posY, double posZ, double distanceSq)
    {
        double d4 = (aa.waypointX - aa.posX) / distanceSq;
        double d5 = (aa.waypointY - aa.posY) / distanceSq;
        double d6 = (aa.waypointZ - aa.posZ) / distanceSq;
        
        AxisAlignedBB axisalignedbb = aa.boundingBox.copy();

        for (int i = 1; (double)i < distanceSq; ++i)
        {
            axisalignedbb.offset(d4, d5, d6);

            if (!aa.worldObj.getCollidingBoundingBoxes(aa, axisalignedbb).isEmpty())
            {
                return false;
            }
        }

        return true;
    }
	
	
	// Already known to have riding and movement upgrades and being ridden right now
	private static void doRiding(Entity_AA turret)
	{
		if (!(turret.riddenByEntity instanceof EntityLivingBase)) { return; }	// Not ridden by a living thing. Tsk tsk.
		
		EntityLivingBase rider = (EntityLivingBase) turret.riddenByEntity;
		
		// Look where the rider is looking
		turret.rotationPitch = turret.updateRotation(turret.rotationPitch, rider.rotationPitch, 30.0f);
		turret.rotationYaw = turret.updateRotation(turret.rotationYaw, rider.rotationYaw, 30.0f);
		
		//System.out.println("[MOVEMENT] Pitch is " + turret.rotationPitch + " / rider pitch is " + rider.rotationPitch);
		
		//turret.rotationYaw = rider.rotationYaw;		// Face where the rider is facing // turret.prevRotationYaw = 
		//turret.rotationPitch = rider.rotationPitch;	// Look where they're looking	//  * 0.5F
		
		// Assuming the rider's speed
		float strafe = rider.moveStrafing;	// Half strafing?	//  * 0.5F
        float forward = rider.moveForward;

        if (forward <= 0.0F) { forward *= 0.25F; }

        turret.stepHeight = 1.0F;
        turret.jumpMovementFactor = turret.getAIMoveSpeed() * 0.1F;

    	turret.moveEntityWithHeading(strafe, forward);
	}
}
