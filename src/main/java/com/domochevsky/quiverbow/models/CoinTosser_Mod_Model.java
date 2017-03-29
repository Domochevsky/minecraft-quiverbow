package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class CoinTosser_Mod_Model extends ModelBase
{
 	// Parts
 	ModelRenderer Barrel;
 	ModelRenderer Stock1;
 	ModelRenderer Stock2;
 	ModelRenderer Ammo;
 	ModelRenderer Trigger;
   
	public CoinTosser_Mod_Model()
	{
	 	textureWidth = 64;
	 	textureHeight = 32;
	 	
	 	Barrel = new ModelRenderer(this, 0, 24);
	    Barrel.addBox(0F, 0F, 0F, 18, 4, 4);
	    Barrel.setRotationPoint(0F, -1F, -1F);
	    Barrel.setTextureSize(64, 32);
	    Barrel.mirror = true;
	    setRotation(Barrel, 0F, 0F, 0F);
	    
	    Stock1 = new ModelRenderer(this, 45, 27);
	    Stock1.addBox(0F, 0F, 0F, 7, 3, 2);
	    Stock1.setRotationPoint(-6F, 1F, 0F);
	    Stock1.setTextureSize(64, 32);
	    Stock1.mirror = true;
	    setRotation(Stock1, 0F, 0F, 0F);
	    
	    Stock2 = new ModelRenderer(this, 45, 27);
	    Stock2.addBox(0F, 0F, 0F, 7, 3, 2);
	    Stock2.setRotationPoint(-11F, 2F, -0.1F);
	    Stock2.setTextureSize(64, 32);
	    Stock2.mirror = true;
	    setRotation(Stock2, 0F, 0F, 0F);
	    
	    Ammo = new ModelRenderer(this, 0, 18);
	    Ammo.addBox(0F, 0F, 0F, 12, 2, 3);
	    Ammo.setRotationPoint(4F, 3F, -0.5F);
	    Ammo.setTextureSize(64, 32);
	    Ammo.mirror = true;
	    setRotation(Ammo, 0F, 0F, 0F);
	    
	    Trigger = new ModelRenderer(this, 45, 23);
	    Trigger.addBox(0F, 0F, 0F, 5, 2, 1);
	    Trigger.setRotationPoint(-2F, 3F, 0.5F);
	    Trigger.setTextureSize(64, 32);
	    Trigger.mirror = true;
	    setRotation(Trigger, 0F, 0F, 0F);
	}
   
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float tick)
	{
		super.render(entity, x, y, z, yaw, pitch, tick);
		setRotationAngles(x, y, z, yaw, pitch, tick, entity);
	    Barrel.render(tick);
	    Stock1.render(tick);
	    Stock2.render(tick);
	    Ammo.render(tick);
	    Trigger.render(tick);
	}
   
	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
   
 	public void setRotationAngles(float x, float y, float z, float yaw, float pitch, float tick, Entity entity)
 	{
 		super.setRotationAngles(x, y, z, yaw, pitch, tick, entity);
 	}
}
