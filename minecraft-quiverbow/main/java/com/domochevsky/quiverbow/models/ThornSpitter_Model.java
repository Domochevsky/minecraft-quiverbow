package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ThornSpitter_Model extends ModelBase
{
	// Parts
	ModelRenderer Barrel;
	ModelRenderer Barrel2;
	ModelRenderer Stock;
	ModelRenderer Connector;
	ModelRenderer Box;
	ModelRenderer Trigger;
	ModelRenderer Ammo1;
	ModelRenderer Ammo2;
  
	public ThornSpitter_Model()
	{
		textureWidth = 32;
		textureHeight = 32;
		
		Barrel = new ModelRenderer(this, 0, 28);
		Barrel.addBox(0F, 0F, 0F, 13, 2, 2);
		Barrel.setRotationPoint(1F, -1F, 0F);
		Barrel.setTextureSize(32, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 0, 28);
		Barrel2.addBox(0F, 0F, 0F, 13, 2, 2);
		Barrel2.setRotationPoint(1F, 1.5F, 0F);
		Barrel2.setTextureSize(32, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Stock = new ModelRenderer(this, 0, 24);
		Stock.addBox(0F, 0F, 0F, 7, 2, 1);
		Stock.setRotationPoint(-5F, 2F, 0.5F);
		Stock.setTextureSize(32, 32);
		Stock.mirror = true;
		setRotation(Stock, 0F, 0F, -0.3717861F);
		Connector = new ModelRenderer(this, 0, 21);
		Connector.addBox(0F, 0F, 0F, 7, 1, 1);
		Connector.setRotationPoint(1F, 1F, 0.5F);
		Connector.setTextureSize(32, 32);
		Connector.mirror = true;
		setRotation(Connector, 0F, 0F, 0F);
		Box = new ModelRenderer(this, 16, 18);
		Box.addBox(0F, 0F, 0F, 5, 6, 3);
		Box.setRotationPoint(0.7333333F, -1.5F, -0.5F);
		Box.setTextureSize(32, 32);
		Box.mirror = true;
		setRotation(Box, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 7, 18);
		Trigger.addBox(0F, 0F, 0F, 3, 1, 1);
		Trigger.setRotationPoint(-2F, 3F, 0.5333334F);
		Trigger.setTextureSize(32, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Ammo1 = new ModelRenderer(this, 20, 14);
		Ammo1.addBox(0F, 0F, 0F, 4, 1, 2);
		Ammo1.setRotationPoint(1.533333F, 4F, -0.03333334F);
		Ammo1.setTextureSize(32, 32);
		Ammo1.mirror = true;
		setRotation(Ammo1, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 11, 12);
		Ammo2.addBox(0F, 0F, 0F, 3, 4, 1);
		Ammo2.setRotationPoint(2F, 5F, 0.5F);
		Ammo2.setTextureSize(32, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);

	}
  
	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Barrel.render(f5);
		Barrel2.render(f5);
		Stock.render(f5);
		Connector.render(f5);
		Box.render(f5);
		Trigger.render(f5);
		Ammo1.render(f5);
		Ammo2.render(f5);

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
