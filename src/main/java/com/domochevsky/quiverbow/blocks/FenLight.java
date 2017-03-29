package com.domochevsky.quiverbow.blocks;

import java.util.Random;

import com.domochevsky.quiverbow.Helper;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class FenLight extends Block
{
	float size = 0.25F;
	float sizeMin = 0.375F;
	float sizeMax = 0.625F;
	
	public FenLight(Material material) 
	{
		super(material);
		this.setLightLevel(0.95F);	// Light, yo
		this.setHardness(0.2F);
		this.setResistance(10.0F);
		this.setStepSound(soundTypeGlass);
		this.setCreativeTab(CreativeTabs.tabDecorations);
		this.setBlockTextureName("glowstone");
		this.setBlockName("Fen Light");
		//this.setBlockBounds(sizeMin, sizeMin, sizeMin, sizeMax, sizeMax, sizeMax);
	}
	
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
		// Let's try a small cube for starters EDIT: Let's not. No stepping stones here
        /*return AxisAlignedBB.getAABBPool().getAABB(
        		(double)x + this.getBlockBoundsMinX(), (double)y + this.getBlockBoundsMinY(), (double)z + this.getBlockBoundsMinZ(),
        		(double)x + this.getBlockBoundsMaxX(), (double)y + this.getBlockBoundsMaxY(), (double)z + this.getBlockBoundsMaxZ()
        );*/
		return null;
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
		int meta = world.getBlockMetadata(x, y, z);
		
		float plusX = 0;
		float plusY = 0;
		float plusZ = 0;
		
		if (meta == 0)	{ plusY = 0.375F; }			// Bottom, so need to move to the top
		else if (meta == 1) { plusY = -0.375F; } 	// Top, so need to move to the bottom
		else if (meta == 2) { plusZ = 0.375F; }		// East, so need to move west
		else if (meta == 3) { plusZ = -0.375F; }	// West, so need to move east
		else if (meta == 4) { plusX = 0.375F; } 	// North, so need to move south
		else if (meta == 5) { plusX = -0.375F; } 	// South, so need to move north
		
		this.setBlockBounds(sizeMin + plusX, sizeMin + plusY, sizeMin + plusZ, sizeMax + plusX, sizeMax + plusY, sizeMax + plusZ);
    }
	
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
		if (!world.isRemote)
		{
			// Checking here to see if the block we're attached to is valid (and breaking if it isn't)
			int meta = world.getBlockMetadata(x, y, z);	// Contains the side we're attached to
			
			if (meta == 0) { if ( !Helper.hasValidMaterial(world, x, y + 1, z) ) { world.setBlockToAir(x, y, z); } } // Bottom, checking Top
			else if (meta == 1) { if ( !Helper.hasValidMaterial(world, x, y - 1, z) ) { world.setBlockToAir(x, y, z); } } // Top, checking Bottom
			else if (meta == 2) { if ( !Helper.hasValidMaterial(world, x, y, z + 1) ) { world.setBlockToAir(x, y, z); } }	// East
			else if (meta == 3) { if ( !Helper.hasValidMaterial(world, x, y, z - 1) ) { world.setBlockToAir(x, y, z); } }	// West
			else if (meta == 4) { if ( !Helper.hasValidMaterial(world, x + 1, y, z) ) { world.setBlockToAir(x, y, z); } }	// North
			else if (meta == 5) { if ( !Helper.hasValidMaterial(world, x - 1, y, z) ) { world.setBlockToAir(x, y, z); } }	// South
		}
    }
	
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) 
	{
		// If this gets called then someone wants the light to turn back into air, since the timer ran out
		if (!world.isRemote) 
		{ 
			world.setBlockToAir(x, y, z); 
			
			// SFX
	    	for (int i = 0; i < 8; ++i) { world.spawnParticle("slime", x, y, z, 0.0D, 0.0D, 0.0D); }
		}
	}
	
	
	@Override
	public Item getItemDropped(int par1, Random par2rand, int par3) { return null; } // Dropping nothing. We're gonna stay behind the scenes
}
