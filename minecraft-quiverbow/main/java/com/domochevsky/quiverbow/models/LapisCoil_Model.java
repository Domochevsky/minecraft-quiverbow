package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class LapisCoil_Model extends ModelBase
{
	ModelRenderer Barrel2;
	ModelRenderer Stock1;
	ModelRenderer Trigger;
	ModelRenderer Ammo1;
	ModelRenderer Ammo2;
	ModelRenderer Ammo3;
	ModelRenderer Ammo4;
	  
	public LapisCoil_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
	
		Barrel2 = new ModelRenderer(this, 0, 0);
		Barrel2.addBox(0F, 0F, 0F, 18, 4, 3);
		Barrel2.setRotationPoint(-4F, 0F, -0.5F);
		Barrel2.setTextureSize(64, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 8);
		Stock1.addBox(0F, 0F, 0F, 8, 4, 2);
		Stock1.setRotationPoint(-12F, 1F, 0F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 15);
		Trigger.addBox(0F, 0F, 0F, 5, 2, 1);
		Trigger.setRotationPoint(-5F, 4F, 0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Ammo1 = new ModelRenderer(this, 0, 19);
		Ammo1.addBox(0F, 0F, 0F, 2, 4, 2);
		Ammo1.setRotationPoint(1F, 4F, 0F);
		Ammo1.setTextureSize(64, 32);
		Ammo1.mirror = true;
		setRotation(Ammo1, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 0, 19);
		Ammo2.addBox(0F, 0F, 0F, 2, 4, 2);
		Ammo2.setRotationPoint(4F, 4F, 0F);
		Ammo2.setTextureSize(64, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);
		Ammo3 = new ModelRenderer(this, 0, 19);
		Ammo3.addBox(0F, 0F, 0F, 2, 4, 2);
		Ammo3.setRotationPoint(7F, 4F, 0F);
		Ammo3.setTextureSize(64, 32);
		Ammo3.mirror = true;
		setRotation(Ammo3, 0F, 0F, 0F);
		Ammo4 = new ModelRenderer(this, 0, 26);
		Ammo4.addBox(0F, 0F, 0F, 11, 2, 2);
		Ammo4.setRotationPoint(-2F, -2F, 9.992007E-15F);
		Ammo4.setTextureSize(64, 32);
		Ammo4.mirror = true;
		setRotation(Ammo4, 0F, 0F, 0F);
	}
	
	
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Barrel2.render(f5);
		Stock1.render(f5);
		Trigger.render(f5);
		Ammo1.render(f5);
		Ammo2.render(f5);
		Ammo3.render(f5);
		Ammo4.render(f5);
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
