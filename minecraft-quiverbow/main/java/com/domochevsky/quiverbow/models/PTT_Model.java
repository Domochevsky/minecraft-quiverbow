package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class PTT_Model extends ModelBase
{
	// Parts
	ModelRenderer Body;
	ModelRenderer Barrel;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Ammo;
	ModelRenderer Trigger;
	
	public PTT_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Body = new ModelRenderer(this, 0, 0);
		Body.addBox(0F, -2F, -2F, 6, 4, 4);
		Body.setRotationPoint(0F, 0F, 0F);
		Body.setTextureSize(64, 32);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		Barrel = new ModelRenderer(this, 21, 0);
		Barrel.addBox(6F, -1.5F, -1.533333F, 10, 3, 3);
		Barrel.setRotationPoint(0F, 0F, 0F);
		Barrel.setTextureSize(64, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 9);
		Stock1.addBox(-5F, -1.5F, -1F, 6, 3, 2);
		Stock1.setRotationPoint(0F, 0F, 0F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 9);
		Stock2.addBox(-8F, -0.5F, -1F, 6, 3, 2);
		Stock2.setRotationPoint(0F, 0F, 0F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Ammo = new ModelRenderer(this, 0, 15);
		Ammo.addBox(2F, 2F, -0.5F, 3, 4, 1);
		Ammo.setRotationPoint(0F, 0F, 0F);
		Ammo.setTextureSize(64, 32);
		Ammo.mirror = true;
		setRotation(Ammo, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 9, 15);
		Trigger.addBox(-2F, 1F, -0.5F, 3, 2, 1);
		Trigger.setRotationPoint(0F, 0F, 0F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
	}
  
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Body.render(f5);
		Barrel.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Ammo.render(f5);
		Trigger.render(f5);
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
