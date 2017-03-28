package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SeedSweeper_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel;
	ModelRenderer Stock;
	ModelRenderer Trigger;
	ModelRenderer Ammo1;
	ModelRenderer Ammo2;
	ModelRenderer Ammo3;

	public SeedSweeper_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
	
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, 0F, 0F, 18, 3, 3);
		Barrel.setRotationPoint(-4F, 0F, -0.5F);
		Barrel.setTextureSize(64, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Stock = new ModelRenderer(this, 0, 8);
		Stock.addBox(0F, 0F, 0F, 8, 4, 2);
		Stock.setRotationPoint(-12F, 0.5333334F, 0F);
		Stock.setTextureSize(64, 32);
		Stock.mirror = true;
		setRotation(Stock, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 15);
		Trigger.addBox(0F, 0F, 0F, 5, 3, 1);
		Trigger.setRotationPoint(-5F, 3F, 0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Ammo1 = new ModelRenderer(this, 0, 19);
		Ammo1.addBox(0F, 0F, 0F, 1, 5, 4);
		Ammo1.setRotationPoint(11F, 2F, -1F);
		Ammo1.setTextureSize(64, 32);
		Ammo1.mirror = true;
		setRotation(Ammo1, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 0, 19);
		Ammo2.addBox(0F, 0F, 0F, 1, 5, 4);
		Ammo2.setRotationPoint(0F, 2F, -1F);
		Ammo2.setTextureSize(64, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);
		Ammo3 = new ModelRenderer(this, 11, 23);
		Ammo3.addBox(0F, 0F, 0F, 10, 5, 4);
		Ammo3.setRotationPoint(1F, 3F, -1F);
		Ammo3.setTextureSize(64, 32);
		Ammo3.mirror = true;
		setRotation(Ammo3, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float tick)
	{
		super.render(entity, x, y, z, yaw, pitch, tick);
	    setRotationAngles(x, y, z, yaw, pitch, tick, entity);
		Barrel.render(tick);
		Stock.render(tick);
		Trigger.render(tick);
		Ammo1.render(tick);
		Ammo2.render(tick);
		Ammo3.render(tick);
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
